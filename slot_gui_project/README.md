# Slot Simulator 🎰
CSC 131 Final Project – Java desktop slot‑machine game  
*(MVC architecture • Scrum workflow • Continuous Integration)*

## Features
- Spin three reels with classic symbols 🍒 🔔 ⭐ 7  
- Secure RNG (`java.security.SecureRandom`)  
- Pluggable payout strategies  
- Java Swing GUI, MVC separation  
- JUnit 5 tests with ≥ 80 % JaCoCo coverage

## Build & Run
```bash
mvn clean javafx:run      # if you use Maven + JavaFX plugin
# or
./gradlew run             # if you switch to Gradle
