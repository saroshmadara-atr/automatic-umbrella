// SPDX-License-Identifier: AGPL-3.0-or-later
// Based on NearbyGlasses by yjeanrenaud (https://github.com/yjeanrenaud/yj_nearbyglasses)

package com.smartglasses.helper

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import android.util.Log
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SmartGlassesScanner(
    private val context: Context,
    private val config: ScannerConfig = ScannerConfig(),
    private val onDeviceDetected: (SmartGlassesEvent) -> Unit
) {

    data class ScannerConfig(
        val rssiThreshold: Int = -75,
        val debugEnabled: Boolean = false,
        val debugCompanyIds: Set<Int> = emptySet(),
        val onDebugLog: ((String) -> Unit)? = null
    )

    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private var bleScanner: BluetoothLeScanner? = null

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    private fun d(msg: String) {
        if (config.debugEnabled) config.onDebugLog?.invoke(msg)
    }

    private var lastUiDebugAt = 0L

    private fun dThrottled(msg: String, minIntervalMs: Long = 250) {
        if (!config.debugEnabled) return
        val now = System.currentTimeMillis()
        if (now - lastUiDebugAt < minIntervalMs) return
        lastUiDebugAt = now
        config.onDebugLog?.invoke(msg)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            processScanResult(result)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            super.onBatchScanResults(results)
            results.forEach { processScanResult(it) }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e(TAG, "Scan failed with error code: $errorCode")
            _isScanning.value = false
        }
    }

    @SuppressLint("MissingPermission")
    fun startScanning(): Boolean {
        if (!isBluetoothEnabled()) {
            Log.w(TAG, "Bluetooth is not enabled")
            return false
        }

        if (_isScanning.value) {
            Log.w(TAG, "Already scanning")
            return false
        }

        bleScanner = bluetoothAdapter?.bluetoothLeScanner

        if (bleScanner == null) {
            Log.e(TAG, "BLE scanner not available")
            return false
        }

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
            .setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT)
            .setReportDelay(0)
            .build()

        try {
            bleScanner?.startScan(null, scanSettings, scanCallback)
            _isScanning.value = true
            Log.i(TAG, "BLE scanning started with RSSI threshold: ${config.rssiThreshold} dBm")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error starting BLE scan", e)
            return false
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScanning() {
        if (!_isScanning.value) return

        try {
            bleScanner?.stopScan(scanCallback)
            _isScanning.value = false
            Log.i(TAG, "BLE scanning stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping BLE scan", e)
        }
    }

    private fun processScanResult(result: ScanResult) {
        val deviceAddress = result.device.address

        if (result.rssi < config.rssiThreshold) {
            if (config.debugEnabled) {
                d("Filtered RSSI addr=$deviceAddress rssi=${result.rssi}")
            }
            return
        }

        val canReadDeviceIdentity =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED

        val deviceName: String? = when {
            !result.scanRecord?.deviceName.isNullOrBlank() -> result.scanRecord?.deviceName
            canReadDeviceIdentity && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                try {
                    result.device.alias
                } catch (_: SecurityException) {
                    null
                }
            }
            else -> null
        }

        val manufacturerData = result.scanRecord?.manufacturerSpecificData
        var companyId: Int? = null
        var manufacturerDataHex: String? = null

        if (manufacturerData != null && manufacturerData.size() > 0) {
            companyId = manufacturerData.keyAt(0)
            val data = manufacturerData.valueAt(0)
            manufacturerDataHex = data.joinToString("") { "%02X".format(it) }
        }

        val nameSafe = deviceName ?: "?"
        val companySafe = companyId?.let { "0x%04X".format(it) } ?: "none"
        dThrottled(
            "ADV addr=$deviceAddress name=$nameSafe rssi=${result.rssi} " +
                    "companyId=$companySafe len=${manufacturerDataHex?.length?.div(2) ?: 0}"
        )

        val scanRecord = result.scanRecord

        val uuidInServiceList = scanRecord?.serviceUuids.orEmpty().any { parcelUuid ->
            parcelUuid.uuid == SmartGlassesIdentifier.PRIMARY_SERVICE_UUID
        }

        val uuidInServiceData = scanRecord?.serviceData?.keys.orEmpty().any { parcelUuid ->
            parcelUuid.uuid == SmartGlassesIdentifier.PRIMARY_SERVICE_UUID
        }

        val hasPrimaryService = uuidInServiceList || uuidInServiceData

        val (isSmartGlassesReal, reasonReal) =
            SmartGlassesIdentifier.isSmartGlasses(companyId, deviceName, hasPrimaryService)

        val overrideMatch =
            config.debugEnabled && companyId != null && config.debugCompanyIds.contains(companyId)

        val isSmartGlasses = isSmartGlassesReal || overrideMatch
        val reason = when {
            overrideMatch -> "Debug override: Company ID 0x%04X matched".format(companyId)
            else -> reasonReal
        }

        if (config.debugEnabled) {
            Log.d(
                TAG,
                "ADV addr=$deviceAddress name=$nameSafe rssi=${result.rssi} " +
                        "companyId=$companySafe mfgLen=${manufacturerDataHex?.length?.div(2) ?: 0} " +
                        "smartglasses=$isSmartGlasses reason=$reason"
            )
        }

        if (isSmartGlasses) {
            val event = SmartGlassesEvent(
                timestamp = System.currentTimeMillis(),
                deviceAddress = deviceAddress,
                deviceName = deviceName,
                rssi = result.rssi,
                companyId = companyId?.let { "0x${String.format("%04X", it)}" },
                companyName = companyId?.let { SmartGlassesIdentifier.getCompanyName(it) }
                    ?: "Unknown",
                manufacturerData = manufacturerDataHex,
                detectionReason = reason
            )

            Log.d(TAG, "Smart glasses detected: ${event.deviceName ?: "Unknown"} (${event.rssi} dBm)")
            onDeviceDetected(event)
        }
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    companion object {
        private const val TAG = "SmartGlassesScanner"
    }
}
