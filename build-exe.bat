@echo off
echo Building Windows Executable for Quiz App...

cd src
call gradlew.bat :composeApp:packageDistributionForCurrentOS

echo.
echo Build complete! Your executable should be located in:
echo src\composeApp\build\compose\binaries\main\app\
echo.
pause
