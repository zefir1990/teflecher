# 🧠 Teflecher Quiz App

**Teflecher** is a fast, interactive, cross-platform Quiz Application built natively with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**. It allows users to intuitively load quizzes from local JSON files, answer multi-choice questions, instantly see correctness feedback, and track their scores.

Being a KMP project, it shares 100% of its UI and most of its business logic across Desktop, Web, Android, and iOS targets!

## ✨ Features
* **Cross-Platform User Interface** powered by declarative Jetpack Compose Multiplatform UI.
* **Native File Pickers** using platform-native APIs (`java.awt.FileDialog` on JVM Desktop, and HTML `<input type="file">` on the Web) to flexibly load your own Quiz JSON files dynamically.
* **Fallback Hardcoded Quizzes** for platforms that do not currently have the file picker fully implemented (e.g. Mobile iOS/Android).
* **Interactive Results:** Immediate visual cues (Green for Correct, Red for Incorrect) right when you click.
* **End-Game Score Screen:** Automatically tallies up correct answers out of total questions and provides a "Restart" functionality.

## 🚀 How to Build & Run

### 🌐 Web (WasmJS / JS)
We highly recommend running the web target to test the JSON file-uploading experience. 
If you are on Windows, you can simply run our custom batch script from your root directory:
```shell
build-web.bat
```
This batch script will automatically:
1. Compile the `wasmJsBrowserDistribution` WebAssembly artifact using Gradle.
2. Open your default web browser to `http://localhost:8000`.
3. Launch a local Python web server (`python -m http.server`) to serve the compiled application files correctly (bypassing strict browser CORS blocking on `.wasm` files).

Alternatively, from an IDE or macOS/Linux terminal, run the development server via Gradle:
```shell
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

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

## 📄 JSON Quiz Format
Teflecher takes standard `.json` quiz structures. A sample mock `quiz-example.json` file is included in the directory root to start testing immediately! The format schema is structured like this: 
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

## 🛠️ Tech Stack Layering
* **UI**: Compose Multiplatform (`androidx.compose`)
* **Serialization**: `kotlinx.serialization.json`
* **Concurrency**: Kotlin Coroutines (`kotlinx.coroutines`) 
* **Native Wrappers**: `expect`/`actual` interfaces bridging Kotlin code to standard Java AWT (Desktop API) and Javascript DOM/browser standard APIs (Web).

---
*Created with Kotlin Multiplatform.*
