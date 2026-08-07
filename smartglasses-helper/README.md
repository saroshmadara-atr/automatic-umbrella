# SmartGlasses Helper

A drop-in Android helper library for detecting smart glasses via BLE scanning.
Extracted from [NearbyGlasses](https://github.com/yjeanrenaud/yj_nearbyglasses).

## License

AGPL-3.0-or-later — see the original project's LICENSE.

## Supported Devices

| Product | Identification Method |
|---|---|
| Meta / Ray-Ban | Manufacturer ID 0x01AB, 0x058E |
| EssilorLuxottica (Oakley) | Manufacturer ID 0x0D53 |
| Snap Spectacles | Manufacturer ID 0x03C2 |
| Rogbird VisionPro / Rollme VistaView | Manufacturer ID 0x05D6 (Zhuhai Jieli) |
| Nilox Smart AI Glasses / HeyCyan | Primary Service UUID + device name |

## Files

- `SmartGlassesScanner.kt` — BLE scanner with smart glasses detection logic
- `SmartGlassesIdentifier.kt` — Company IDs, UUIDs, and identification rules
- `SmartGlassesEvent.kt` — Detection event data class (Parcelable)
- `SmartGlassesNotificationHelper.kt` — Optional notification support

## Quick Start

```kotlin
// 1. Create the scanner
val scanner = SmartGlassesScanner(
    context = this,
    config = SmartGlassesScanner.ScannerConfig(
        rssiThreshold = -75
    ),
    onDeviceDetected = { event ->
        Log.d("MyApp", "Detected: ${event.deviceName} (${event.companyName})")
        Log.d("MyApp", "Reason: ${event.detectionReason}")
    }
)

// 2. Start scanning (requires BLE permissions)
scanner.startScanning()

// 3. Observe scanning state
lifecycleScope.launch {
    scanner.isScanning.collect { scanning ->
        // update UI
    }
}

// 4. Stop when done
scanner.stopScanning()
```

## Required Permissions

Add to your `AndroidManifest.xml`:

```xml
<!-- Android 12+ (API 31+) -->
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />

<!-- Android 11 and below -->
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

<!-- Notifications (Android 13+) -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

## Dependencies

```groovy
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3"
implementation "androidx.core:core-ktx:1.12.0"
```
