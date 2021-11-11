package tictactoe.game;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tictactoe.game.entity.Game;
import tictactoe.game.entity.Game.GameState;
import tictactoe.game.entity.Game.PlayerType;
import tictactoe.game.entity.GameRepository;
import tictactoe.user.entity.AppUser;

@Component
public class GameService {

    private final GameRepository gameRepository;

    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    // TODO: transactional to keep delete & save
    public Game create(AppUser appUser, boolean playerGoFirst) {

        gameRepository.deleteUserGames(appUser);

        Game game = new Game();
        game.setAppUser(appUser);
        game.setState(GameState.IN_PROGRESS);
        game.setNextMove("PLAYER_1"); // TODO: should this be an enum ?

        if (playerGoFirst) {
            game.setPlayer1Type(PlayerType.HUMAN);
            game.setPlayer2Type(PlayerType.COMPUTER);
        } else {
            game.setPlayer1Type(PlayerType.COMPUTER);
            game.setPlayer2Type(PlayerType.HUMAN);
        }

        game.setRows(BoardUtil.createEmpty());

        gameRepository.save(game);

        return game;
    }

    public void update(Game game) {
        gameRepository.save(game);
    }

    public Game getLastGame(AppUser appUser) {
        return gameRepository.findFirstByAppUserOrderByIdDesc(appUser);
    }

    public void takeTurnRandom(Game game) {
        String tileId = BoardUtil.getRandomAvailableTile(game.getRows());
        if (tileId != null) {
            takeTurn(game, tileId);
        }
    }

    public void takeTurn(Game game, String tileId) {
        if (game.getState() != GameState.IN_PROGRESS) {
            return; // TODO: is this necessary? Can only  mark if Game is in progress (aka no winner or draw)
        }

        String[] indices = tileId.split("-");
        if (indices.length != 2) {
            return;
        }

        BoardTile tile;
        if (game.getNextMove().equalsIgnoreCase("PLAYER_1")) {
            tile = BoardTile.X;
            game.setNextMove("PLAYER_2");
        } else {
            tile = BoardTile.O;
            game.setNextMove("PLAYER_1");
        }

        int rowIndex = Integer.parseInt(indices[0]);
        int columnIndex = Integer.parseInt(indices[1]);
        game.getRows().get(rowIndex).set(columnIndex, tile.toString());

        GameState state = evaluateGameState(game.getRows());
        game.setState(state);
        if (state != GameState.IN_PROGRESS) {
            game.setNextMove(null); // Game over. Null out
        }

        update(game);
    }

    private GameState evaluateGameState(List<List<String>> rows) {
        for (List<String> line : BoardUtil.getAllLines(rows)) {
            String firstTile = line.get(0);
            if (firstTile.isEmpty()) {
                continue;
            }

            if (line.stream().allMatch(tile -> tile.equals(firstTile))) {
                return firstTile.equals(BoardTile.X.toString()) ? GameState.PLAYER_1_WIN : GameState.PLAYER_2_WIN;
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
