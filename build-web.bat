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
echo import http.server > serve.py
echo import socketserver >> serve.py
echo PORT = 8000 >> serve.py
echo class Handler(http.server.SimpleHTTPRequestHandler): >> serve.py
echo     extensions_map = http.server.SimpleHTTPRequestHandler.extensions_map.copy() >> serve.py
echo     extensions_map['.wasm'] = 'application/wasm' >> serve.py
echo with socketserver.TCPServer(("", PORT), Handler) as httpd: >> serve.py
echo     print("Serving at port", PORT) >> serve.py
echo     httpd.serve_forever() >> serve.py

python serve.py

pause
