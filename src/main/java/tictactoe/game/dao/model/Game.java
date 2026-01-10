package tictactoe.game.dao.model;

import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tictactoe.user.dao.model.AppUser;

@Setter
@Getter
@Entity
public class Game {

    public enum PlayerType {
        HUMAN,
        COMPUTER;
    }

    public enum PlayerNumber {
        PLAYER_1,
        PLAYER_2
    }

    public enum GameState {
        IN_PROGRESS,
        PLAYER_1_WIN,
        PLAYER_2_WIN,
        DRAW
    }

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private AppUser appUser;

    private PlayerType player1Type;

    private PlayerType player2Type;

    private PlayerNumber nextMove;

    private GameState state;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<List<String>> rows;

}
