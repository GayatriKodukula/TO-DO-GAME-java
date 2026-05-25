@echo off
mkdir out 2>nul
javac -d out -sourcepath src src\todogame\*.java src\todogame\ui\*.java
java -cp out todogame.ui.GameUI
