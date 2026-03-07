@echo off
echo Navigating to project root...
cd /d "%~dp0src"

echo Building Kotlin/Wasm distribution...
call gradlew.bat :composeApp:wasmJsBrowserDistribution

echo.
echo Build complete!
echo Navigating to output directory...
cd composeApp\build\dist\wasmJs\productionExecutable

echo.
echo Opening browser...
start http://localhost:8000

echo Starting local web server (requires Python to serve WebAssembly correctly)...
python -m http.server 8000

pause
