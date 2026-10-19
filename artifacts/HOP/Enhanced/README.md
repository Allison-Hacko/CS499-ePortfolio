# HOP: Health Objectives and Progress

## Project Overview

HOP is an Android application developed in Java to help users manage personal health goals and track progress. The application includes weight tracking, profile information, a diary, and notification-related features. Its interface uses a pink theme with a sakura-inspired visual style.

## Main Features

- User registration and login
- User profile management
- Weight entry and weight history
- Health diary functionality
- Notification-related functionality
- Local SQLite database storage

## Software Design and Engineering Enhancements

The enhanced version improves password handling and defensive input validation. Passwords are processed with PBKDF2-HMAC-SHA256 using random 16-byte salts and 256-bit derived hashes. Stored hashes are compared using `MessageDigest.isEqual`.

The password utility rejects invalid inputs and malformed hexadecimal values. The login check also rejects missing or incorrectly sized stored credentials and handles malformed hexadecimal credentials without allowing that input error to propagate through the login check.

## Testing

The project includes automated unit tests for password utility behavior, including:

- Salt length and salt uniqueness
- Repeatable hashing for the same password and salt
- Different hashes for different salts
- Expected hash length
- Hexadecimal conversion and malformed input handling
- Invalid password hashing inputs

## Build and Test

Requirements include a compatible Java development environment and the Android SDK versions configured by the project.

From the project root, run:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

The debug APK is generated at:

`app/build/outputs/apk/debug/app-debug.apk`

## Submission Notes

This package contains the enhanced project source and unit tests. Generated build files and machine-specific configuration are excluded.

The APK is a debug build, not a production-signed release. Successful automated builds and unit tests do not by themselves confirm that every feature has been tested on a physical device.
