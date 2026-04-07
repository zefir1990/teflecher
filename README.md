# 🧠 Teflecher Quiz App

**Teflecher** is a fast, interactive, cross-platform Quiz Application built natively with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**. It allows users to intuitively load quizzes from local JSON files or remote URLs, answer multi-choice questions, instantly see correctness feedback, and track their scores.

Being a KMP project, it shares 100% of its UI and most of its business logic across Desktop, Web, Android, and iOS targets!

🌍 **Live WebAssembly Demo:** [demensdeum.com/software/teflecher/](https://demensdeum.com/software/teflecher/)

## ✨ Features
* **Cross-Platform User Interface** powered by declarative Jetpack Compose Multiplatform UI.
* **Native File Pickers** using platform-native APIs to flexibly load your own local Quiz JSON files dynamically.
* **Remote Quiz Fetching** fetches a list of available quizzes from `quiz-list.json` securely over the network.
* **Interactive Results:** Immediate visual cues (Green for Correct, Red for Incorrect) right when you click.
* **Localization & Persistence:** Full English and Russian localization. Your language preference is automatically saved locally using `Multiplatform Settings` and persists across app restarts!
* **End-Game Score Screen:** Automatically tallies up correct answers out of total questions and provides a "Restart" functionality, as well as an option to retry only the questions you answered incorrectly.

## 🚀 How to Build & Run

### 🌐 Web (WasmJS)
The Web target runs completely natively in the browser using WebAssembly.
If you are on Windows, you can simply run our custom batch script from your root directory:
```shell
build-web.bat
```
This batch script will automatically:
1. Compile the `wasmJsBrowserDistribution` WebAssembly artifact using Gradle.
2. Open your default web browser to `http://localhost:8000`.
3. Launch a robust, dedicated local Python web server (`serve.py`) to serve the compiled application files with the strictly required `application/wasm` MIME type.

### 💻 Desktop (JVM)
The desktop app comes fully featured with file system integration for the quiz JSON file picker.
```shell
# Mac / Linux
./gradlew :composeApp:run

# Windows
.\gradlew.bat :composeApp:run
```

### 📱 Android & iOS (Mobile)
You can build and deploy directly to mobile emulators/devices right from Android Studio or Xcode!
* **Android:** Run the `:composeApp:assembleDebug` task or click "Run" in your IDE using the Android App build configuration.
* **iOS:** Open the integrated Xcode project located at `./iosApp/` and click the "Run" button, or use the KMP plugin's iOS run configuration inside IntelliJ/Android Studio!

## 🌍 Web Deployment (Nginx / Production)
When hosting the compiled `productionExecutable` WebAssembly application on a live server (like Nginx), the server **must** be configured to return the `application/wasm` MIME type for `.wasm` files. If it returns `application/octet-stream`, the browser will refuse to execute the app for security reasons.

If you don't have access to your server's configuration, Teflecher includes a JavaScript polyfill in `index.html` that automatically intercepts the download and bypasses the MIME type check entirely—meaning the app will work out of the box regardless of server configuration! 

**To deploy:**
1. Run the build command (`build-web.bat` or `./gradlew :composeApp:wasmJsBrowserDistribution`).
2. Upload **ALL FILES** located in `src/composeApp/build/dist/wasmJs/productionExecutable/` to your web server. 
*Note: Webpack generates unique hashes for the WASM files on every build. You must delete the old files on your server and upload the entire new folder at once, otherwise old JS files will attempt to request outdated WASM chunks.*

## 📄 JSON Quiz Format
Teflecher takes standard `.json` quiz structures. The format schema is structured like this: 
```json
{
  "id": "quiz-001",
  "title": "Compose Multiplatform Basics",
  "questions": [
    {
      "id": "question-001",
      "text": "What is Compose Multiplatform?",
      "answers": [
        {"id": "answer-001", "text": "A declarative UI framework", "isCorrect": true},
        {"id": "answer-002", "text": "A database ORM", "isCorrect": false}
      ]
    }
  ]
}
```

Quiz file names also use a language suffix in the form `-<language code>`. For example, `-en` means the quiz uses English localization.

## 🛠️ Tech Stack Layering
* **UI**: Compose Multiplatform (`androidx.compose`)
* **Network**: Ktor Client (`io.ktor`)
* **Serialization**: `kotlinx.serialization.json`
* **Storage**: Multiplatform Settings (`com.russhwolf:multiplatform-settings`)
* **Concurrency**: Kotlin Coroutines (`kotlinx.coroutines`) 

---
*Created with Kotlin Multiplatform.*
