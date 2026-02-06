package tictactoe.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import tictactoe.game.dao.model.Game;
import tictactoe.game.dao.model.Game.GameState;
import tictactoe.user.dao.AppUserRepository;
import tictactoe.user.dao.model.AppUser;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    MockMvcTester mockMvcTester;

    @MockitoBean
    GameService mockGameService;

    @MockitoBean
    AppUserRepository mockUserRepository;

    @MockitoBean
    Principal mockPrincipal;

    @BeforeEach
    void setUp() {
        when(mockPrincipal.getName()).thenReturn("evil-morty");
    }

    @Test
    void index_NoLastGame_NewGameCreated() {
        // given
        AppUser appUser = new AppUser();
        when(mockUserRepository.findByUsername(any())).thenReturn(appUser);

        Game noLastGame = null;
        when(mockGameService.getLastGame(any())).thenReturn(noLastGame);

        boolean playerGoFirst = true;
        Game newGame = GameService.newGameInstance(appUser, playerGoFirst);
        when(mockGameService.create(any(), anyBoolean())).thenReturn(newGame);

        // when
        assertThat(mockMvcTester.get().uri("/").principal(mockPrincipal))
                // then
                .hasStatusOk()
                .hasViewName("index")
                .model()
                    .containsEntry("playerGoFirst", playerGoFirst)
                    .containsEntry("playStatus", "IN_PROGRESS")
                    .containsEntry("board", newGame.getRows());

        verify(mockUserRepository, only()).findByUsername(mockPrincipal.getName());
        verify(mockGameService).getLastGame(appUser);
        verify(mockGameService).create(appUser, true);
        verifyNoMoreInteractions(mockGameService);
    }

    @Test
    void index_InvalidUser_ThrowException() {
        // given
        AppUser noAppUser = null;
        when(mockUserRepository.findByUsername(any())).thenReturn(noAppUser);

        // when
        assertThat(mockMvcTester.get().uri("/").principal(mockPrincipal))
                // then
                .failure()
                .hasCauseInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Invalid username: " + mockPrincipal.getName());

        verify(mockUserRepository, only()).findByUsername(mockPrincipal.getName());
        verifyNoInteractions(mockGameService);
    }

    @Test
    void takeTurns_NewGamePlayerGoFirst_EmptyBoardState() {
        // given
        String tileId = "not-applicable"; // user clicked "New Game"
        boolean newGame = true;
        boolean playerGoFirst = true;

        AppUser appUser = new AppUser();
        when(mockUserRepository.findByUsername(any())).thenReturn(appUser);

        Game game = GameService.newGameInstance(appUser, playerGoFirst);
        when(mockGameService.create(any(), anyBoolean())).thenReturn(game);

        // when
        assertThat(mockMvcTester.post()
                .uri("/")
                .principal(mockPrincipal)
                .param("tile-id", tileId)
                .param("new-game", String.valueOf(newGame))
                .param("player-go-first", String.valueOf(playerGoFirst))
        ) // then
                .hasStatusOk()
                .hasViewName("index")
                .model()
                    .containsEntry("playerGoFirst", playerGoFirst)
                    .containsEntry("playStatus", "IN_PROGRESS")
                    .containsEntry("board", game.getRows());

        verify(mockUserRepository, only()).findByUsername(mockPrincipal.getName());
        verify(mockGameService, only()).create(appUser, playerGoFirst);
    }

    @Test
    void takeTurns_NewGameComputerGoFirst_ComputerTakesCenter() {
        // given
        String tileId = "not-applicable"; // user clicked "New Game"
        boolean newGame = true;
        boolean playerGoFirst = false; // computer goes first

        AppUser appUser = new AppUser();
        when(mockUserRepository.findByUsername(any())).thenReturn(appUser);

        Game game = GameService.newGameInstance(appUser, playerGoFirst);
        when(mockGameService.create(any(), anyBoolean())).thenReturn(game);

        // when
        assertThat(mockMvcTester.post()
                .uri("/")
                .principal(mockPrincipal)
                .param("tile-id", tileId)
                .param("new-game", String.valueOf(newGame))
                .param("player-go-first", String.valueOf(playerGoFirst))
        ) // then
                .hasStatusOk()
                .hasViewName("index")
                .model()
                .containsEntry("playerGoFirst", playerGoFirst)
                .containsEntry("playStatus", "IN_PROGRESS")
                .containsEntry("board", game.getRows());

        verify(mockUserRepository, only()).findByUsername(mockPrincipal.getName());
        verify(mockGameService).create(appUser, playerGoFirst);
        verify(mockGameService).takeTurn(game, "1-1"); // computer always picks center
        verifyNoMoreInteractions(mockGameService);
    }

    @Test
    void takeTurns_InProgressGame_LoadLastGameMakePlayerAndComputerTurns() {
        // given
        String tileId = "0-1";
        AppUser appUser = new AppUser();
        when(mockUserRepository.findByUsername(any())).thenReturn(appUser);

        Game lastGame = GameService.newGameInstance(appUser, false);
        when(mockGameService.getLastGame(any())).thenReturn(lastGame);

        // when
        assertThat(mockMvcTester.post()
                .uri("/")
                .principal(mockPrincipal)
                .param("tile-id", tileId)
        ) // then
                .hasStatusOk()
                .hasViewName("index")
                .model()
                .containsEntry("playerGoFirst", false)
                .containsEntry("playStatus", "IN_PROGRESS")
                .containsEntry("board", lastGame.getRows());

        verify(mockUserRepository, only()).findByUsername(mockPrincipal.getName());
        verify(mockGameService).getLastGame(appUser);
        verify(mockGameService).takeTurn(lastGame, tileId); // player move
        verify(mockGameService).takeTurnRandom(lastGame); // computer move
        verifyNoMoreInteractions(mockGameService);
    }

    @Test
    void setModelGameAttributes_GameInProgress_ExpectedModelState() {
        // given
        Model model = new ExtendedModelMap();
        boolean playerGoFirst = true;
        Game game = GameService.newGameInstance(new AppUser(), playerGoFirst);

        // when
        GameController.setModelGameAttributes(model, game);

        // then
        assertThat(model.asMap())
                .containsEntry("playerGoFirst", playerGoFirst)
                .containsEntry("playStatus", "IN_PROGRESS")
                .containsEntry("board", game.getRows());
    }

    @Test
    void setModelGameAttributes_GamePlayer1Win_ExpectedModelState() {
        // given
        Model model = new ExtendedModelMap();
        boolean playerGoFirst = true;
        Game game = GameService.newGameInstance(new AppUser(), playerGoFirst);
        game.setState(GameState.PLAYER_1_WIN);

        // when
        GameController.setModelGameAttributes(model, game);

        // then
        assertThat(model.asMap())
                .containsEntry("playerGoFirst", playerGoFirst)
                .containsEntry("playStatus", "WON")
                .containsEntry("board", game.getRows());
    }

    @Test
    void setModelGameAttributes_GamePlayer2Win_ExpectedModelState() {
        // given
        Model model = new ExtendedModelMap();
        boolean playerGoFirst = true;
        Game game = GameService.newGameInstance(new AppUser(), playerGoFirst);
        game.setState(GameState.PLAYER_2_WIN);

        // when
        GameController.setModelGameAttributes(model, game);

        // then
        assertThat(model.asMap())
                .containsEntry("playerGoFirst", playerGoFirst)
                .containsEntry("playStatus", "LOST")
                .containsEntry("board", game.getRows());
    }

    @Test
    void setModelGameAttributes_GameDraw_ExpectedModelState() {
        // given
        Model model = new ExtendedModelMap();
        boolean playerGoFirst = false;
        Game game = GameService.newGameInstance(new AppUser(), playerGoFirst);
        game.setState(GameState.DRAW);

        // when
        GameController.setModelGameAttributes(model, game);

        // then
        assertThat(model.asMap())
                .containsEntry("playerGoFirst", playerGoFirst)
                .containsEntry("playStatus", "DRAW")
                .containsEntry("board", game.getRows());
    }
}
