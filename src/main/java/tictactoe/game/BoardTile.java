package tictactoe.game;

enum BoardTile {

    EMPTY(""),
    X("x"),
    O("o");

    private final String text;

    BoardTile(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
