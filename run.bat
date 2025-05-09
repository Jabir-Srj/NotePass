@echo off
echo Building NotePass...
call mvn clean package
if errorlevel 1 (
    echo Build failed!
    pause
    exit /b 1
)

echo Running NotePass...
call mvn javafx:run
pause 