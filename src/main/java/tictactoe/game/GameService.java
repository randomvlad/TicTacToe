package tictactoe.game;

import java.util.List;
import java.util.regex.Pattern;
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

    private static final Pattern TILE_ID_PATTERN = Pattern.compile("\\d{1,2}-\\d{1,2}");

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

    /**
     * Mark tile {@code tileId} for the player whose turn it is.
     *
     * @param tileId tile ID in format "{rowIndex}-{columnIndex}". Example: "0-1" (row 0, column 1).
     * @return {@code true} if the turn was taken. {@code false} (and game is left unchanged) if game is over,
     * {@code tileId} is malformed or out of bounds, or the tile is already taken.
     */
    public boolean takeTurn(Game game, String tileId) {
        if (game.getState() != GameState.IN_PROGRESS) {
            return false;
        }

        if (tileId == null || !TILE_ID_PATTERN.matcher(tileId).matches()) {
            return false;
        }

        String[] indices = tileId.split("-");
        int rowIndex = Integer.parseInt(indices[0]);
        int columnIndex = Integer.parseInt(indices[1]);

        List<List<String>> rows = game.getRows();
        if (rowIndex >= rows.size() || columnIndex >= rows.get(rowIndex).size()) {
            return false;
        }

        if (!rows.get(rowIndex).get(columnIndex).isEmpty()) {
            return false; // tile already taken
        }

        BoardTile tile;
        if (game.getNextMove() == PlayerNumber.PLAYER_1) {
            tile = BoardTile.X;
            game.setNextMove(PlayerNumber.PLAYER_2);
        } else {
            tile = BoardTile.O;
            game.setNextMove(PlayerNumber.PLAYER_1);
        }

        rows.get(rowIndex).set(columnIndex, tile.getValue());

        GameState state = evaluateGameState(game.getRows());
        game.setState(state);
        if (state != GameState.IN_PROGRESS) {
            game.setNextMove(null); // Game over. Null out
        }

        gameRepository.save(game);
        return true;
    }

    /**
     * @return {@code true} if game is in progress and the next move belongs to a human player.
     */
    public static boolean isHumanTurn(Game game) {
        if (game.getState() != GameState.IN_PROGRESS || game.getNextMove() == null) {
            return false;
        }
        PlayerType nextPlayerType = game.getNextMove() == PlayerNumber.PLAYER_1
                ? game.getPlayer1Type()
                : game.getPlayer2Type();
        return nextPlayerType == PlayerType.HUMAN;
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
