package tictactoe.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tictactoe.game.entity.Game;
import tictactoe.game.entity.Game.GameState;
import tictactoe.game.entity.Game.PlayerNumber;
import tictactoe.game.entity.Game.PlayerType;
import tictactoe.game.entity.GameRepository;
import tictactoe.user.entity.AppUser;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    private GameService service;

    @Mock
    private GameRepository mockRepository;

    @BeforeEach
    public void setUp() {
        service = new GameService(mockRepository);
    }

    @Test
    void create_PlayerGoFirst_Player1TypeHuman() {
        boolean playerGoFirst = true;
        AppUser appUser = new AppUser();

        Game game = service.create(appUser, playerGoFirst);

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
        boolean playerGoFirst = false;
        AppUser appUser = new AppUser();

        Game game = service.create(appUser, playerGoFirst);

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
        AppUser appUser = new AppUser();
        service.getLastGame(appUser);
        verify(mockRepository).findFirstByAppUserOrderByIdDesc(appUser);
    }

    @Test
    void takeTurn_NewGameTwoMoves_GameInProgress() {
        Game game = service.create(new AppUser(), true);

        service.takeTurn(game, "1-1");

        assertThat(game.getNextMove()).isEqualTo(PlayerNumber.PLAYER_2);
        assertThat(game.getState()).isEqualTo(GameState.IN_PROGRESS);
        assertRows(//@formatter:off
                game,
                "", "", "",
                "", "x", "",
                "", "", ""
        );//@formatter:on

        service.takeTurn(game, "0-1");

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
        Game game = service.create(new AppUser(), true);
        game.getRows().set(0, Arrays.asList("x", "x", ""));

        service.takeTurn(game, "0-2");

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
        Game game = service.create(new AppUser(), true);
        game.getRows().set(0, Arrays.asList("x", "x", "o"));
        game.getRows().set(1, Arrays.asList("", "o", ""));
        game.getRows().set(2, Arrays.asList("", "", ""));

        service.takeTurn(game, "1-0"); // player 1 marks X
        service.takeTurn(game, "2-0"); // player 2 marks O and wins

        assertThat(game.getNextMove()).isNull();
        assertThat(game.getState()).isEqualTo(GameState.PLAYER_2_WIN);
    }

    @Test
    void takeTurn_NoWinners_GameOverDraw() {
        Game game = service.create(new AppUser(), true);
        game.getRows().set(0, Arrays.asList("o", "x", "x"));
        game.getRows().set(1, Arrays.asList("x", "o", "o"));
        game.getRows().set(2, Arrays.asList("o", "", "x"));

        service.takeTurn(game, "2-1");

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
}
