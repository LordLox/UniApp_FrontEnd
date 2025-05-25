# UniApp

**UniApp** is an Android application designed for university environments, facilitating interactions between students, professors, and administrators. It appears to support features like event management, user profiles, and QR code-based interactions.

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Setup and Installation](#setup-and-installation)
- [Building the Application](#building-the-application)
- [CI/CD](#cicd)
- [Contributing](#contributing)
- [License](#license)

## Features

Based on the project structure, UniApp includes the following features:

* **User Authentication:**
    * Login screen for users (`LoginActivity.kt`, `login_screen.xml`).
* **Role-Based Access:**
    * Separate homepages and functionalities for different user roles:
        * **Students:** Profile management (`ProfileStudActivity.kt`, `profile_stud.xml`).
        * **Professors:** Dedicated homepage (`HomepageProfessorActivity.kt`, `homepage_professor.xml`) and profile (`ProfileProfActivity.kt`, `profile_prof.xml`).
        * **Admins:** Admin dashboard (`AdminHomeActivity.kt`, `admin_home.xml`) and user management capabilities (`UserActivity.kt`, `user.xml`).
* **Event Management:**
    * View a list of events (`EventListActivity.kt`, `event_list.xml`).
    * View event details (`EventActivity.kt`, `event.xml`).
* **QR Code Functionality:**
    * Generate and display QR codes (`QrCodePageActivity.kt`, `qr_page.xml`).
    * Scan and read QR codes (`ReadQrPageActivity.kt`, `read_qr_page.xml`), likely using CameraX and ML Kit Barcode Scanning.
    * Pop-ups for QR code scan results (`popup_qr_code_ok.xml`, `popup_qr_code_no.xml`).
* **Networking:**
    * Interacts with a backend API for data (evident from `network` package and DTOs like `UserInfo.kt`, `EventDto.kt`, `HistoryDto.kt`, `BarcodeDataDto.kt`).

## Technologies Used

This project is built using modern Android development technologies:

* **Programming Language:** Kotlin
* **Build Tool:** Gradle
* **Android SDK:**
    * Min SDK: 24
    * Target SDK: 34
    * Compile SDK: 34
* **Core Libraries:**
    * AndroidX (Core KTX, AppCompat, Activity, ConstraintLayout)
    * Material Components for UI
* **Networking:**
    * Retrofit
    * Gson (for JSON parsing with Retrofit)
    * OkHttp (HTTP client, often used by Retrofit)
    * Volley
* **Asynchronous Programming:**
    * Kotlin Coroutines (Core & Android)
* **Camera and Vision:**
    * CameraX (Core, Camera2, Lifecycle, View) for camera operations.
    * ML Kit Barcode Scanning for reading QR codes.
* **Security:**
    * Tink (Cryptography library)
* **Java Compatibility:** Java 1.8 compatibility.

## Project Structure

The project follows a standard Android application structure:

```
UniApp_FrontEnd/
├── app/                        # Main application module
│   ├── build.gradle.kts        # App-level Gradle build script
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/uniapp/
│   │   │   │   ├── model/      # Data Transfer Objects (DTOs)
│   │   │   │   ├── network/    # API service interfaces (Retrofit)
│   │   │   │   ├── util/       # Utility classes
│   │   │   │   └── *.kt        # Activity classes (UI logic)
│   │   │   ├── res/            # Resources (layouts, drawables, strings, etc.)
│   │   │   │   ├── layout/     # XML layout files for activities and UI components
│   │   │   │   └── ...
│   │   │   └── AndroidManifest.xml # Application manifest file
│   │   ├── androidTest/      # Instrumentation tests
│   │   └── test/           # Unit tests
├── build.gradle.kts            # Project-level Gradle build script
├── gradle/wrapper/             # Gradle wrapper files
├── gradle.properties           # Project-wide Gradle settings
├── settings.gradle.kts         # Gradle settings script
└── local.properties            # Local configuration (SDK path, etc. - typically not versioned)
```

## Setup and Installation

To set up the project locally, follow these steps:

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/](https://github.com/)<your-username>/UniApp_FrontEnd.git
    cd UniApp_FrontEnd
    ```
2.  **Open in Android Studio:**
    * Open Android Studio (ensure you have a recent version).
    * Click on "Open" or "Import Project".
    * Navigate to the cloned `UniApp_FrontEnd` directory and select it.
3.  **Gradle Sync:** Android Studio should automatically start syncing the project with Gradle. This might take a few minutes.
4.  **Configure `local.properties`:** If not automatically created or if `sdk.dir` is missing, you might need to create a `local.properties` file in the `UniApp_FrontEnd` root directory and specify the path to your Android SDK:
    ```properties
    sdk.dir=/path/to/your/android/sdk
    ```
    (Android Studio usually handles this automatically).

## Building the Application

### From Android Studio

1.  Select the `app` configuration from the run configurations dropdown.
2.  Choose a target device (emulator or physical device).
3.  Click the "Run" button (green play icon) to build and run the debug version.
4.  To build a release APK:
    * Go to `Build > Generate Signed Bundle / APK...`.
    * Select "APK" and click "Next".
    * Choose or create a keystore (refer to project documentation or create a new one for local builds).
    * Select the "release" build variant.
    * Click "Finish". The signed APK will be located in `UniApp_FrontEnd/app/build/outputs/apk/release/`.

### From Command Line (within `UniApp_FrontEnd` directory)

* **Build Debug APK:**
    ```bash
    ./gradlew :app:assembleDebug
    ```
    The APK will be in `UniApp_FrontEnd/app/build/outputs/apk/debug/`.

* **Build Release APK:**
    ```bash
    ./gradlew :app:assembleRelease
    ```
    This requires signing configurations to be set up in your `build.gradle.kts` or provided via command-line properties/environment variables (as done in the CI/CD pipeline). The unsigned APK (if signing isn't fully configured locally) will be in `UniApp_FrontEnd/app/build/outputs/apk/release/`.

## CI/CD

This project includes a GitHub Actions workflow (`.github/workflows/android_release.yml`) for:

* Building the Android application on pushes to tags (e.g., `v1.0.0`).
* Signing the release APK using secrets stored in GitHub Actions.
* Creating a GitHub Release and uploading the signed APK as an artifact.

Refer to the workflow file and the project's GitHub Actions secrets configuration for more details on the release process.

## Contributing

Contributions are welcome! If you'd like to contribute, please follow these steps:

1.  Fork the repository.
2.  Create a new branch (`git checkout -b feature/your-feature-name`).
3.  Make your changes.
4.  Commit your changes (`git commit -m 'Add some feature'`).
5.  Push to the branch (`git push origin feature/your-feature-name`).
6.  Open a Pull Request.

Please ensure your code adheres to the project's coding standards and includes tests where applicable.

## License

This project is licensed under the MIT License - see the `LICENSE.md` file for details (if applicable).

---