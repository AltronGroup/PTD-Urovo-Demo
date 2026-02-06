# Urovo Customer Demo

A comprehensive Android demonstration application showcasing Urovo hardware features and Cendroid Application integration. This app provides practical examples for developers working with Urovo devices, demonstrating how to access and utilize various hardware components and system APIs.

## 📱 Overview

The Urovo Customer Demo application is designed to help developers understand and implement Urovo device functionality in their own applications. It serves as both a learning tool and a reference implementation for integrating with Urovo's specialized hardware and the Cendroid framework.

## ✨ Features

This demo application includes working examples for the following Urovo hardware components:

### 🔍 **Scanner/Camera**
- Barcode scanning functionality using device cameras
- QR code reading with ZXing integration
- Real-time camera preview and image capture

### 📄 **Printer**
- Thermal printer integration (available on supported devices)
- Print text and formatted content
- Printer status monitoring

### 💳 **Magnetic Card Reader (MAG)**
- Magnetic stripe card reading
- Track data parsing and display
- Card swipe event handling

### 🏧 **ICC (Integrated Circuit Card)**
- Smart card reader integration
- IC card communication protocols
- Card insertion/removal detection

### 📡 **PICC (Proximity Integrated Circuit Card) / NFC**
- Contactless card reading
- NFC tag detection and reading
- ISO 14443 protocol support

### 📱 **Device Information**
- System information retrieval
- Device model and specifications
- Hardware capability detection
- Cendroid API availability checking

### 🔗 **Cendroid Application Invocation**
- Examples of invoking Cendroid system applications
- Intent-based communication with system services
- Integration patterns for Urovo-specific functionality

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Arctic Fox (2020.3.1) or later
- **JDK**: Java 11 or higher
- **Android SDK**: API Level 27 (Android 8.1) or higher
- **Urovo Device**: Physical Urovo hardware device for testing (emulator has limited functionality)
- **Gradle**: 8.13.2 (included via wrapper)

## 📖 Usage Guide

### Main Menu
Upon launching the app, you'll see a main menu with buttons for each hardware feature. Buttons are automatically enabled/disabled based on your device's available hardware capabilities.

### Feature Examples

1. **Scanner/Camera Demo**
   - Tap "Scanner" to open the barcode scanning interface
   - Point camera at a barcode or QR code
   - View scanned data in real-time

2. **Printer Demo**
   - Access printer functions for supported devices
   - Test print functionality with sample text
   - Check printer status and troubleshoot

3. **Card Reader Demos**
   - **MAG**: Swipe magnetic cards to read track data
   - **ICC**: Insert smart cards to communicate with chip
   - **PICC/NFC**: Tap contactless cards or NFC tags

4. **Device Info**
   - View comprehensive device information
   - Check hardware capabilities
   - Verify Cendroid API availability

5. **Cendroid Invocation**
   - Learn how to invoke Cendroid system applications
   - See example Intent configurations
   - Test integration with system services

## 🏗️ Project Structure

```
Urovo Customer Demo/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/coldstone/urovocustomerdemo/
│   │       │   ├── MainActivity.java           # Main entry point
│   │       │   ├── CameraActivity.java         # Scanner/Camera demo
│   │       │   ├── PrinterActivity.java        # Printer demo
│   │       │   ├── MagActivity.java            # Magnetic card reader
│   │       │   ├── IccActivity.java            # Smart card reader
│   │       │   ├── NfcActivity.java            # NFC/PICC demo
│   │       │   ├── DeviceInfoActivity.java     # Device information
│   │       │   ├── InvokeActivity.java         # Cendroid invocation
│   │       │   └── sdk/                        # Urovo SDK wrapper classes
│   │       ├── res/                            # Resources (layouts, strings, etc.)
│   │       └── AndroidManifest.xml             # App configuration
│   ├── libs/
│   │   └── platform_sdk_v4.1.0326.jar          # Urovo Platform SDK
│   └── build.gradle.kts                        # Module build configuration
├── gradle/                                      # Gradle wrapper and dependencies
├── build.gradle.kts                            # Project build configuration
└── README.md                                   # This file
```

## 🔧 Configuration

### Target Devices
- **Minimum SDK**: API 27 (Android 8.1 Oreo)
- **Target SDK**: API 36
- **Optimized for**: Urovo handheld devices with specialized hardware

### Key Dependencies
- AndroidX AppCompat & Material Design
- CameraX for camera functionality
- ZXing for barcode/QR code processing
- Urovo Platform SDK v4.1.0326

## 📚 API Reference

### Urovo SDK
The application uses the Urovo Platform SDK (`platform_sdk_v4.1.0326.jar`) which provides APIs for:
- Scanner and barcode reading
- Printer control
- Card reader operations (MAG, ICC, PICC)
- Device management
- System service integration

### UrovoManager
The `UrovoManager` class (in `sdk/` package) provides a simplified interface to check hardware availability:
- `isPrinterAvailable()` - Check for thermal printer
- `isMagAvailable()` - Check for magnetic card reader
- `isIccAvailable()` - Check for IC card reader
- `isPiccAvailable()` - Check for NFC/PICC reader
- `isDeviceManagerAvailable()` - Check for device management APIs

## 🐛 Troubleshooting

### Common Issues

**Issue**: Buttons are disabled on the main screen  
**Solution**: The device doesn't have that specific hardware. This is expected behavior on devices without that capability.

**Issue**: Scanner doesn't work  
**Solution**: Grant camera permissions when prompted. Check device settings to ensure camera access is allowed.

**Issue**: Printer/Card reader doesn't respond  
**Solution**: Ensure the Cendroid framework is properly installed on your Urovo device. Some features require system-level integration.

**Issue**: Build fails with SDK errors  
**Solution**: Verify that `platform_sdk_v4.1.0326.jar` is present in the `app/libs/` directory.

## 🤝 Contributing

This demo application is intended as a reference implementation. To extend or customize:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/YourFeature`)
3. **Commit** your changes (`git commit -m 'Add some feature'`)
4. **Push** to the branch (`git push origin feature/YourFeature`)
5. **Open** a Pull Request

### Development Guidelines
- Follow Android coding conventions
- Add comments for complex hardware interactions
- Test on actual Urovo devices when possible
- Update README for new features

## 📄 License

Please refer to your Urovo SDK license agreement for usage terms and conditions.

## 📞 Support

For Urovo SDK documentation and support:
- **Urovo Technology**: Contact your Urovo sales representative
- **Technical Documentation**: Refer to Urovo Platform SDK documentation
- **Device-Specific Issues**: Contact Urovo technical support

## 🔖 Version History

- **v1.0** (Current)
  - Initial release
  - Scanner/Camera integration
  - Printer support
  - Card reader demos (MAG, ICC, PICC/NFC)
  - Device information display
  - Cendroid application invocation examples

---

**Note**: This application is designed specifically for Urovo devices and requires Urovo hardware for full functionality. Some features may not work on standard Android devices or emulators.
