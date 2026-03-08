@echo off
echo Navigating to project root...
cd /d "%~dp0src"

echo Building Kotlin/Wasm distribution...
call gradlew.bat :composeApp:wasmJsBrowserDistribution
if %ERRORLEVEL% neq 0 (
    echo.
    echo Build failed! Aborting web server startup.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo Build complete!
echo Navigating to output directory...
cd composeApp\build\dist\wasmJs\productionExecutable

echo.
echo Opening browser...
start http://localhost:8000

echo Starting local web server with proper WASM MIME type...
python "%~dp0serve.py"

pause
