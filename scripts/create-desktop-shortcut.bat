@echo off
:: Automatically creates a Desktop shortcut to run-topstep-token.bat

set SCRIPT_DIR=%~dp0
set SHORTCUT_NAME=Run Topstep Token.lnk
set BATCH_FILE=%SCRIPT_DIR%run-topstep-token.bat

:: Get the current user's Desktop path
for /f "usebackq tokens=2,*" %%A in (`reg query "HKCU\Software\Microsoft\Windows\CurrentVersion\Explorer\Shell Folders" /v Desktop`) do set DESKTOP=%%B

:: Create the shortcut using PowerShell
powershell -NoProfile -Command ^
  "$WshShell = New-Object -ComObject WScript.Shell;" ^
  "$Shortcut = $WshShell.CreateShortcut('$DESKTOP\\%SHORTCUT_NAME%');" ^
  "$Shortcut.TargetPath = '%BATCH_FILE%';" ^
  "$Shortcut.WorkingDirectory = '%SCRIPT_DIR%';" ^
  "$Shortcut.Save()"

echo Shortcut created on your Desktop as "%SHORTCUT_NAME%".
pause
