@echo off
setlocal

call gradlew.bat runConsole
if errorlevel 1 goto end

:end
endlocal
