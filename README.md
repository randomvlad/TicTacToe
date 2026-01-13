# Tic-tac-toe App

A secured web app to play Tic-tac-toe against a dummy computer opponent.

## Features & Notes
* Play a game on a 3x3 board with an option to go first or after the computer opponent.
* Computer opponent's AI chooses random squares, except when going first in which case the center tile is always picked.
* User game data is persisted to an in-memory database. As long as the server is not restarted, a player can leave and return to finish an in-progress game.  
* App is secured with a username & password login. Database is seeded with two usernames `rick` and `morty`. Both have the same password `pickle`.
* UI renders server-side in the name of simplicity. Each game move results in a full page refresh.
* For more info about the project and lessons learned, see: [Little Code Gems](docs/code-gems.md).
* Unit tests: [src/test/java/tictactoe/*](src/test/java/tictactoe)

## Tech Stack
| | Technology |
|---|---|
| __Language__ | Java 25 |
| __Framework__ | Spring Boot v4.0 |
| __Data Layer__ | H2 Database, JPA & Hibernate v7.2 | 
| __UI Layer__ | HTML, CSS, Javascript, [Bootstrap](https://getbootstrap.com/) v5, [Thymeleaf](http://www.thymeleaf.org/) v3.1 |
| __Testing__ | JUnit 5, Mockito, AssertJ |
| __Build Tool__ | Gradle v9.2 |

## Install & Run
* Install Java 25.
  * Tip: use [SDKMAN!](https://sdkman.io/install/) to effortlessly install and switch between Java versions and distros.
* Clone repo: `git clone https://github.com/randomvlad/TicTacToe.git`
* Navigate `cd TicTacToe` and run applicable [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html#sec:using_wrapper) command:
  * macOS/Unix: `./gradlew bootRun`
  * Windows: `gradlew.bat bootRun`
* Once app is running, go to [http://localhost:8080/tictactoe/](http://localhost:8080/tictactoe/).
* Log in with username `rick` or `morty` and password `pickle` to play a game.
* To end app, kill process in terminal with `CTRL + C`. 

## Game Screenshots
<img src="docs/images/tictactoe_screenshot_login.png" style="width: 800px; height: 700px;" alt="Tic-tac-toe app login screenshot" />
<br />
<img src="docs/images/tictactoe_screenshot_win.png" style="width: 800px; height: 700px;" alt="Tic-tac-toe app won game screenshot" />
<br />
<img src="docs/images/tictactoe_screenshot_loss.png" style="width: 800px; height: 700px;" alt="Tic-tac-toe app lost game screenshot" />
