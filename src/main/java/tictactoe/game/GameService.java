package tictactoe.game;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tictactoe.game.dao.GameRepository;
import tictactoe.game.dao.model.Game;
import tictactoe.game.dao.model.Game.GameState;
import tictactoe.game.dao.model.Game.PlayerNumber;
import tictactoe.game.dao.model.Game.PlayerType;
import tictactoe.game.model.BoardTile;
import tictactoe.user.dao.model.AppUser;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    /**
     * Create and persist a new game for {@code appUser}. Previous
     * game for the user is deleted.
     */
    @Transactional
    public Game create(AppUser appUser, boolean playerGoFirst) {
        gameRepository.deleteUserGames(appUser);
        Game game = newGameInstance(appUser, playerGoFirst);
        gameRepository.save(game);
        return game;
    }

    public Game getLastGame(AppUser appUser) {
        return gameRepository.findFirstByAppUserOrderByIdDesc(appUser);
    }

    /**
     * Mark a random available tile on the game board. No operation, if board has no available tiles or game is over.
     */
    public void takeTurnRandom(Game game) {
        String tileId = BoardUtil.getRandomAvailableTile(game.getRows());
        if (tileId != null) {
            takeTurn(game, tileId);
        }
    }

    public void takeTurn(Game game, String tileId) {
        if (game.getState() != GameState.IN_PROGRESS) {
            return;
        }

        String[] indices = tileId.split("-");
        if (indices.length != 2) {
            return;
        }

        BoardTile tile;
        if (game.getNextMove() == PlayerNumber.PLAYER_1) {
            tile = BoardTile.X;
            game.setNextMove(PlayerNumber.PLAYER_2);
        } else {
            tile = BoardTile.O;
            game.setNextMove(PlayerNumber.PLAYER_1);
        }

        int rowIndex = Integer.parseInt(indices[0]);
        int columnIndex = Integer.parseInt(indices[1]);
        game.getRows().get(rowIndex).set(columnIndex, tile.getValue());

        GameState state = evaluateGameState(game.getRows());
        game.setState(state);
        if (state != GameState.IN_PROGRESS) {
            game.setNextMove(null); // Game over. Null out
        }

        gameRepository.save(game);
    }

    public static Game newGameInstance(AppUser appUser, boolean playerGoFirst) {
        Game game = new Game();
        game.setAppUser(appUser);
        game.setState(GameState.IN_PROGRESS);
        game.setNextMove(PlayerNumber.PLAYER_1);

        if (playerGoFirst) {
            game.setPlayer1Type(PlayerType.HUMAN);
            game.setPlayer2Type(PlayerType.COMPUTER);
        } else {
            game.setPlayer1Type(PlayerType.COMPUTER);
            game.setPlayer2Type(PlayerType.HUMAN);
        }

        game.setRows(BoardUtil.createEmpty());

        return game;
    }

    private GameState evaluateGameState(List<List<String>> rows) {
        for (List<String> line : BoardUtil.getAllLines(rows)) {
            String firstTile = line.getFirst();
            if (firstTile.isEmpty()) {
                continue;
            }

            if (line.stream().allMatch(tile -> tile.equals(firstTile))) {
                return firstTile.equals(BoardTile.X.getValue()) ? GameState.PLAYER_1_WIN : GameState.PLAYER_2_WIN;
            }
        }

        for (List<String> row : rows) {
            if (row.stream().anyMatch(String::isEmpty)) {
                return GameState.IN_PROGRESS;
            }
        }

        return GameState.DRAW; // no connected lines for a winner AND all tiles are taken
    }
}
