# Tic Tac Toe

A secured web app to play Tic Tac Toe against a dummy computer opponent.

## Features & Notes:
* Play a game on a 3x3 board with an option to go first or after the computer opponent.
* Computer opponent's AI is simple and chooses squares at random (except when going first, then center tile is always picked).
* User game data is persisted to an in-memory database.
* App is secured with a username & password login. Database is seeded with two usernames: `rick` and `morty`. Both have the same password `pickle`.
* Unit tests can be found here: [src/test/java/tictactoe/*](src/test/java/tictactoe)
* App is designed to render each time through a full page refresh (in the name of simplicity).
* Player can leave/logout and come back later to finish the game (as long as the server is running).

## Tech Stack:
* Language: Java 11
* Framework: Spring Boot (v2.5.6)
* Data Layer: H2 database, JPA & Hibernate
* UI Layer: HTML, CSS, Javascript, jQuery, [Bootstrap](https://getbootstrap.com/) (v5), [Thymeleaf](http://www.thymeleaf.org/)
* Test Frameworks: JUnit 5, Mockito
* Build Tool: Gradle (v7.2)

## Install & Run:
* Install Java 11.
* Clone repo: `git clone https://github.com/randomvlad/TicTacToe.git`
* Navigate `cd TicTacToe` and run applicable [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html#sec:using_wrapper) command:
  * macOS/Unix: `./gradlew bootRun`
  * Windows: `gradlew.bat bootRun`
* Once app is running, go to [http://localhost:8080/tictactoe/](http://localhost:8080/tictactoe/) and play a game.
* To end app, kill process in terminal with `CTRL + C`. 

## Game Screenshots:
<img src="docs/images/tictactoe_screenshot_login.png" style="width: 800px; height: 700px;" />
<br />
<img src="docs/images/tictactoe_screenshot_win.png" style="width: 800px; height: 700px;" />
<br />
<img src="docs/images/tictactoe_screenshot_loss.png" style="width: 800px; height: 700px;" />
