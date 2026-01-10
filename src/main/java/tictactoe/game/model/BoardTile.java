package tictactoe.game.model;

import lombok.Getter;

@Getter
public enum BoardTile {

    EMPTY(""),
    X("x"),
    O("o");

    private final String value;

    BoardTile(String value) {
        this.value = value;
    }
}
