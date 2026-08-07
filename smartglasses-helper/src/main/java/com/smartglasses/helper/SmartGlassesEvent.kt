// SPDX-License-Identifier: AGPL-3.0-or-later
// Based on NearbyGlasses by yjeanrenaud (https://github.com/yjeanrenaud/yj_nearbyglasses)

package com.smartglasses.helper

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class SmartGlassesEvent(
    val timestamp: Long,
    val deviceAddress: String,
    val deviceName: String?,
    val rssi: Int,
    val companyId: String?,
    val companyName: String,
    val manufacturerData: String?,
    val detectionReason: String
) : Parcelable {

    fun toJson(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return """
            {
                "timestamp": $timestamp,
                "timestampFormatted": "${dateFormat.format(Date(timestamp))}",
                "deviceAddress": "$deviceAddress",
                "deviceName": ${deviceName?.let { "\"$it\"" } ?: "null"},
                "rssi": $rssi,
                "companyId": ${companyId?.let { "\"$it\"" } ?: "null"},
                "companyName": "$companyName",
                "manufacturerData": ${manufacturerData?.let { "\"$it\"" } ?: "null"},
                "detectionReason": "$detectionReason"
            }
        """.trimIndent()
    }

    fun toLogString(): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val time = dateFormat.format(Date(timestamp))
        val name = deviceName ?: "Unknown"
        return "[$time] $name (${rssi}dBm) - $detectionReason"
    }
}
