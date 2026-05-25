#!/bin/bash
mkdir -p out
javac -d out -sourcepath src src/todogame/*.java src/todogame/ui/*.java
java -cp out todogame.ui.GameUI
