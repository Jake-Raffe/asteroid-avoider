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

#### General:
- Press `SPACE` to start or pause the game.
- Press `R` to reset the game.
- Game speed will increase over time.

#### Tetris:

- Falling shapes can be moved with the `LEFT` and `RIGHT` arrow keys.
- Rotate the shape clockwise and anticlockwise using the `W` and `Q` keys.
- When a shape collides with the base or with the existing structure,
  the falling shape is added to the existing structure.
  A new random shape will then be generated.
- Press the `DOWN` arrow key to immediately descend the shape till collision.
- The game will end when the structure extends above the red threshold line.
- If a full line of existing blocks exists, that line of blocks will be removed.
  This gives you more breathing room below the threshold and so is the main priority of gameplay.

#### Asteroid Avoider: _Vertical scrolling game_

- Move the spaceship with the `LEFT` and `RIGHT` arrow keys to avoid the asteroids.
- Collision with an asteroid will invert the colours and end the game.

#### Box Jumper: _Horizontal scrolling game_
(pretty minimal for now, may use it as a base to expand upon later)

- Obstacles will come from the right side of the screen.
- Press the `UP` button to jump over them.

---

#### Future Games:
- Minesweeper: mouse-based since all previous have been keyboard-based.
- Shape cutter: you must cut across a shape, filling in the parts you cut till 90% of the shape is filled in.
  If you or your line is hit by the randomly moving hazards you lose a life.
- Tron: multiplayer game using the same line collision logic as shape cutter.
