package tictactoe;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import tictactoe.model.Game;

@Controller
@SessionAttributes("game")
public class TicTacToeController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index( @ModelAttribute("game" ) Game game ) {
		return "index";
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String markTile( 
			@ModelAttribute("game" ) Game game, 
			@RequestParam("tile_id") String tileId,
			@RequestParam(value = "new_game", required = false, defaultValue = "false") boolean newGame,
			@RequestParam(value = "player_go_first", required = false, defaultValue = "false") boolean playerGoFirst 
			) {
		
		if ( newGame ) {
			game.reset();
			game.setPlayerGoFirst( playerGoFirst );
			if ( !playerGoFirst ) {
				// give computer a small advantage by always placing X in the center as its first move
				game.markTile( "1-1" );
			}
		} else {
			game.markTile( tileId ); // Player Turn
			
			game.markTileRandom(); // Computer Turn
		}
		
		return "index";
	}
	
	@ModelAttribute("game")
	public Game populateGame() {
		// populate object for first time if null (new session)
		// See: http://stackoverflow.com/questions/2757198/spring-framework-3-and-session-attributes
		return new Game(); 
	}

}