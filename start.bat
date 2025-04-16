@echo off

REM Указываем путь к OpenCV .jar файлу
set OPENCV_JAR_PATH="G:\OpenCV\opencv\build\java\opencv-411.jar"

REM Указываем путь к вашему приложению .jar
set APP_JAR="G:\PokerScreen\PokerScreen.jar"

REM Устанавливаем Java путь
set JAVA_PATH=C:\Program Files\Java\jdk-21\bin\java.exe

REM Запуск приложения с указанием CLASSPATH
"%JAVA_PATH%" -Djava.library.path="G:\OpenCV\opencv\build\java\x64\opencv_java4110.dll" -cp "%OPENCV_JAR_PATH%;%APP_JAR%" alex.valker91.Main

pause