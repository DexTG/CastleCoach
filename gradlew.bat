@ECHO OFF
SET DIR=%~dp0
SET WRAPPER_JAR=%DIR%gradle\wrapper\gradle-wrapper.jar
"%JAVA_HOME%\bin\java.exe" -jar "%WRAPPER_JAR%" %*
