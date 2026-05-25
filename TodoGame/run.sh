#!/bin/bash
# Compile and run the TODO Game
mkdir -p out
javac -d out -sourcepath src src/todogame/*.java
java -cp out todogame.Main
