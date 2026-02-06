package tictactoe.game;

import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tictactoe.game.dao.model.Game;
import tictactoe.game.dao.model.Game.PlayerType;
import tictactoe.user.dao.AppUserRepository;
import tictactoe.user.dao.model.AppUser;

@Controller
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    private final AppUserRepository appUserRepository;

    @GetMapping("/")
    public String index(Principal principal, Model model) {
        AppUser appUser = getAppUser(principal);

        Game game = gameService.getLastGame(appUser);
        if (game == null) {
            game = gameService.create(appUser, true);
        }

        setModelGameAttributes(model, game);

        return "index";
    }

    @PostMapping("/")
    public String takeTurns(
            Model model,
            Principal principal,
            @RequestParam("tile-id") String tileId,
            @RequestParam(value = "new-game", required = false, defaultValue = "false") boolean newGame,
            @RequestParam(value = "player-go-first", required = false, defaultValue = "false") boolean playerGoFirst
    ) {
        AppUser appUser = getAppUser(principal);

        Game game;
        if (newGame) {
            game = gameService.create(appUser, playerGoFirst);

            if (!playerGoFirst) {
                // give computer a small advantage by always placing X in the center as its first move
                gameService.takeTurn(game, "1-1");
            }
        } else {
            game = gameService.getLastGame(appUser);
            gameService.takeTurn(game, tileId); // Player Turn
            gameService.takeTurnRandom(game); // Computer Turn
        }

        setModelGameAttributes(model, game);

        return "index";
    }

    static void setModelGameAttributes(Model model, Game game) {
        boolean playerGoFirst = game.getPlayer1Type() == PlayerType.HUMAN;

        String playerStatus = switch (game.getState()) {
            case PLAYER_1_WIN -> playerGoFirst ? "WON" : "LOST";
            case PLAYER_2_WIN -> playerGoFirst ? "LOST" : "WON";
            case DRAW -> "DRAW";
            default -> "IN_PROGRESS";
        };

        model.addAttribute("playerGoFirst", playerGoFirst);
        model.addAttribute("playStatus", playerStatus);
        model.addAttribute("board", game.getRows());
    }

    private AppUser getAppUser(Principal principal) {
        AppUser appUser = appUserRepository.findByUsername(principal.getName());
        if (appUser == null) {
            throw new UsernameNotFoundException("Invalid username: " + principal.getName());
        }
        return appUser;
    }
}
