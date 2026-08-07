// SPDX-License-Identifier: AGPL-3.0-or-later
// Based on NearbyGlasses by yjeanrenaud (https://github.com/yjeanrenaud/yj_nearbyglasses)

package com.smartglasses.helper

import java.util.UUID

object SmartGlassesIdentifier {

    // Meta Platforms, Inc. (formerly Facebook) — Ray-Ban Meta
    const val META_COMPANY_ID1 = 0x01AB
    const val META_COMPANY_ID2 = 0x058E

    // EssilorLuxottica — Oakley and some newer Meta models
    const val ESSILOR_COMPANY_ID = 0x0D53

    // Snap Inc. — Spectacles
    const val SNAP_COMPANY_ID = 0x03C2

    // Zhuhai Jieli Technology — Rogbird VisionPro, Rollme VistaView
    const val ZHUHAI_COMPANY_ID = 0x05D6

    // HeyCyan / Nilox Smart AI Glasses primary service UUID
    const val PRIMARY_SERVICE_UUID_STRING = "7905FFF0-B5CE-4E99-A40F-4B1E122D00D0"
    val PRIMARY_SERVICE_UUID: UUID = UUID.fromString(PRIMARY_SERVICE_UUID_STRING)

    private val KNOWN_NAME_PATTERNS = listOf("rayban", "ray-ban", "ray ban", "heycyan")

    fun isSmartGlasses(
        companyId: Int?,
        deviceName: String?,
        hasPrimaryService: Boolean = false
    ): Pair<Boolean, String> {
        val reasons = mutableListOf<String>()

        when (companyId) {
            META_COMPANY_ID1 -> reasons.add("Meta Company ID (0x01AB)")
            META_COMPANY_ID2 -> reasons.add("Meta Company ID (0x058E)")
            ESSILOR_COMPANY_ID -> reasons.add("EssilorLuxottica Company ID (0x0D53)")
            SNAP_COMPANY_ID -> reasons.add("Snap Company ID (0x03C2)")
            ZHUHAI_COMPANY_ID -> reasons.add("Zhuhai Jieli Technology ID (0x05D6)")
        }

        if (hasPrimaryService) {
            reasons.add("Primary service UUID ($PRIMARY_SERVICE_UUID_STRING)")
        }

        deviceName?.let { name ->
            val nameLower = name.lowercase()
            KNOWN_NAME_PATTERNS.firstOrNull { nameLower.contains(it) }?.let { match ->
                reasons.add("Device name contains '$match'")
            }
        }

        return Pair(reasons.isNotEmpty(), reasons.joinToString(", "))
    }

    fun getCompanyName(companyId: Int): String {
        return when (companyId) {
            META_COMPANY_ID1, META_COMPANY_ID2 -> "Meta Platforms, Inc."
            ESSILOR_COMPANY_ID -> "EssilorLuxottica"
            SNAP_COMPANY_ID -> "Snap Inc."
            ZHUHAI_COMPANY_ID -> "Zhuhai Jieli Technology"
            else -> "Unknown (0x${String.format("%04X", companyId)})"
        }
    }
}
