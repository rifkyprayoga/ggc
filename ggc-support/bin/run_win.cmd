@echo off
set JAVA_EXE=java


echo.  Build Startup
%JAVA_EXE% -classpath atech-tools-0.2.1.jar com.atech.update.startup.BuildStartupFile

call run_ggc.bat




