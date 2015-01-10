@ECHO OFF

SETLOCAL
set JAVA_PATH="C:\Program Files\Java\jdk1.6.0_27\bin"

IF EXIST %JAVA_PATH% SET PATH=%PATH%;%JAVA_PATH%
ENDLOCAL

java -cp build/classes planer.Planer %*

pause