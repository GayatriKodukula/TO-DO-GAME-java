@echo off
REM Compile and run the TODO Game (Windows)
mkdir out 2>nul
javac -d out -sourcepath src src\todogame\*.java
java -cp out todogame.Main
