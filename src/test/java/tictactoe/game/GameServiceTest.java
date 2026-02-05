package tictactoe.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tictactoe.game.dao.GameRepository;
import tictactoe.game.dao.model.Game;
import tictactoe.game.dao.model.Game.GameState;
import tictactoe.game.dao.model.Game.PlayerNumber;
import tictactoe.game.dao.model.Game.PlayerType;
import tictactoe.game.model.BoardTile;
import tictactoe.user.dao.model.AppUser;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @InjectMocks
    private GameService service;

    @Mock
    private GameRepository mockRepository;

    @Test
    void create_PlayerGoFirst_Player1TypeHuman() {
        // given
        boolean playerGoFirst = true;
        AppUser appUser = new AppUser();

        // when
        Game game = service.create(appUser, playerGoFirst);


        // then
        assertThat(game.getAppUser()).isSameAs(appUser);
        assertThat(game.getState()).isEqualTo(GameState.IN_PROGRESS);
        assertThat(game.getNextMove()).isEqualTo(PlayerNumber.PLAYER_1);
        assertThat(game.getPlayer1Type()).isEqualTo(PlayerType.HUMAN);
        assertThat(game.getPlayer2Type()).isEqualTo(PlayerType.COMPUTER);
        assertThat(game.getRows()).isEqualTo(BoardUtil.createEmpty());

        verify(mockRepository).deleteUserGames(appUser);
        verify(mockRepository).save(any());
    }

    @Test
    void create_PlayerGoSecond_Player2TypeHuman() {
        // given
        boolean playerGoFirst = false;
        AppUser appUser = new AppUser();

        // when
        Game game = service.create(appUser, playerGoFirst);

        // then
        assertThat(game.getAppUser()).isSameAs(appUser);
        assertThat(game.getState()).isEqualTo(GameState.IN_PROGRESS);
        assertThat(game.getNextMove()).isEqualTo(PlayerNumber.PLAYER_1);
        assertThat(game.getPlayer1Type()).isEqualTo(PlayerType.COMPUTER);
        assertThat(game.getPlayer2Type()).isEqualTo(PlayerType.HUMAN);
        assertThat(game.getRows()).isEqualTo(BoardUtil.createEmpty());

        verify(mockRepository).deleteUserGames(appUser);
        verify(mockRepository).save(any());
    }

    @Test
    void getLastGame_Call_DelegateToRepository() {
        // given
        AppUser appUser = new AppUser();

        // when
        service.getLastGame(appUser);

        // then
        verify(mockRepository, only()).findFirstByAppUserOrderByIdDesc(appUser);
    }

    @Test
    void takeTurnRandom_NewGameAllTilesEmpty_OneTileMarked() {
        // given
        Game game = service.create(new AppUser(), true);

        // when
        service.takeTurnRandom(game);

        // then
        int countEmpty = 0;
        int countX = 0;
        for (List<String> row : game.getRows()) {
            for (String tile : row) {
                if (tile.equals(BoardTile.X.getValue())) {
                    countX++;
                } else {
                    countEmpty++;
                }
            }
        }

        assertThat(countEmpty).isEqualTo(8);
        assertThat(countX).isEqualTo(1);
    }

    @Test
    void takeTurn_NewGameTwoMoves_GameInProgress() {
        // given
        Game game = service.create(new AppUser(), true);

        // when
        service.takeTurn(game, "1-1");

        // then
        assertThat(game.getNextMove()).isEqualTo(PlayerNumber.PLAYER_2);
        assertThat(game.getState()).isEqualTo(GameState.IN_PROGRESS);
        assertRows(//@formatter:off
                game,
                "", "", "",
                "", "x", "",
                "", "", ""
        );//@formatter:on

        // when
        service.takeTurn(game, "0-1");

        // then
        assertThat(game.getNextMove()).isEqualTo(PlayerNumber.PLAYER_1);
        assertThat(game.getState()).isEqualTo(GameState.IN_PROGRESS);
        assertRows(//@formatter:off
                game,
                "", "o", "",
                "", "x", "",
                "", "", ""
        );//@formatter:on
    }

    @Test
    void takeTurn_Player1Wins_GameOverPlayer1Win() {
        // given
        Game game = service.create(new AppUser(), true);
        setRow(game, 0, "x", "x", "");

        // when
        service.takeTurn(game, "0-2");

        // then
        assertThat(game.getNextMove()).isNull();
        assertThat(game.getState()).isEqualTo(GameState.PLAYER_1_WIN);
        assertRows(//@formatter:off
                game,
                "x", "x", "x",
                "", "", "",
                "", "", ""
        );//@formatter:on
    }

    @Test
    void takeTurn_Player2Wins_GameOverPlayer2Win() {
        // given
        Game game = service.create(new AppUser(), true);
        setRow(game, 0, "x", "x", "o");
        setRow(game, 1, "", "o", "");
        setRow(game, 2, "", "", "");

        // when
        service.takeTurn(game, "1-0"); // player 1 marks X
        service.takeTurn(game, "2-0"); // player 2 marks O and wins

        // then
        assertThat(game.getNextMove()).isNull();
        assertThat(game.getState()).isEqualTo(GameState.PLAYER_2_WIN);
    }

    @Test
    void takeTurn_NoWinners_GameOverDraw() {
        // given
        Game game = service.create(new AppUser(), true);
        setRow(game, 0, "o", "x", "x");
        setRow(game, 1, "x", "o", "o");
        setRow(game, 2, "o", "", "x");

        // when
        service.takeTurn(game, "2-1");

        // then
        assertThat(game.getNextMove()).isNull();
        assertThat(game.getState()).isEqualTo(GameState.DRAW);
    }

    /**
     * Helper assert method to verify expected game rows.
     */
    private void assertRows(Game game, String... tiles) {
        List<List<String>> rows = game.getRows();
        assertThat(rows.get(0)).containsExactly(tiles[0], tiles[1], tiles[2]);
        assertThat(rows.get(1)).containsExactly(tiles[3], tiles[4], tiles[5]);
        assertThat(rows.get(2)).containsExactly(tiles[6], tiles[7], tiles[8]);
    }

    private void setRow(Game game, int rowIndex, String value0, String value1, String value2) {
        List<String> row = game.getRows().get(rowIndex);
        row.set(0, value0);
        row.set(1, value1);
        row.set(2, value2);
    }
}
