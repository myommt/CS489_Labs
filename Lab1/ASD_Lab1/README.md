# Java CLI Application

This is a simple Java command-line application project.

## Structure
- `src/main/java` - Main application source code
- `src/test/java` - Test source code

## Build & Run

If using Maven:
```
mvn compile
mvn exec:java -Dexec.mainClass=Main
```

If using Gradle:
```
gradle build
java -cp build/classes/java/main Main
```

Or compile and run manually:
```
javac src/main/java/Main.java
java -cp src/main/java Main
```
