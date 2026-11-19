# Petruk Othello

A simple Java Othello player with a Swing GUI and an alpha-beta computer opponent.

## Requirements

- Java 8 or newer
- Maven

## Compile

From the project directory, run:

```powershell
mvn clean package
```

This creates the executable JAR at:

```text
target/petruk-1.0.0.jar
```

## Run

Start the GUI with:

```powershell
java -jar target/petruk-1.0.0.jar
```

By default, the computer plays white and the human player plays black.

To let the computer play black instead:

```powershell
java -cp target/petruk-1.0.0.jar com.petruk.othello.GUIPetruk black
```

The `self` argument starts the built-in self-play mode:

```powershell
java -cp target/petruk-1.0.0.jar com.petruk.othello.GUIPetruk self
```

## Controls

- Click a valid square to place a stone.
- Use **Game → New Game** to choose the computer's color.
- Use **Game → Exit** to close the application.
