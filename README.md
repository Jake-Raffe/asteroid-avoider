# Asteroid Avoider

#### Originally intended for just a vertical scrolling asteroid avoider game, this app now contains multiple simple games built in Scala using ScalaFX.
___

### Set Up and Run
The app requires a standard sbt set up. Use `sbt compile` and `sbt run` to run.

Running the application will prompt you in the shell to choose which game to run.
The game will then open in a new window.

Alternatively, a specific game can be run by the IDE from within its object file.
___

### How to Play

#### Common:
- Press `SPACE` to start or pause the game.
- Press `R` to reset the game.
- Game speed will increase over time.

#### Asteroid Avoider:

- Move the spaceship with the `LEFT` and `RIGHT` arrow keys to avoid the asteroids.
- Collision with an asteroid will invert the colours and end the game.

#### Tetris:

- Falling shapes can be moved with the `LEFT` and `RIGHT` arrow keys.
- When a shape collides with the base or with the existing structure,
  the falling shape is added to the existing structure.
  A new random shape will then be generated.
- Press the `DOWN` arrow key to immediately descend the shape till collision.
- The game will end when the structure extends above the red threshold line.
