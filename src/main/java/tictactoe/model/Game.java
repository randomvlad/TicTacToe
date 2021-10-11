package tictactoe.model;

import java.util.List;

public class Game {

    private final Board board;
    private PlayerState playerState;
    private boolean playerGoFirst;
    private boolean nextMoveX;

    public Game() {
        board = new Board();
        startNew(true);
    }

    /**
     * Reset existing state to start a new game.
     *
     * @param playerGoFirst True if human player will go first
     */
    public void startNew(boolean playerGoFirst) {
        this.playerGoFirst = playerGoFirst;
        this.nextMoveX = true;
        this.playerState = PlayerState.IN_PROGRESS;
        this.board.reset();
    }

    public Board getBoard() {
        return board;
    }

    public void markTile(String tileId) {
        setTileValue(board.get(tileId));
    }

    public void markTileRandom() {
        setTileValue(board.getRandomAvailable());
    }

    public boolean isPlayerGoFirst() {
        return playerGoFirst;
    }

    public boolean isGameOver() {
        return playerState.isGameOver();
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    private void setTileValue(Tile tile) {
        if (isGameOver() || !tile.isEmpty()) {
            return;
        }

        tile.setValue(nextMoveX ? Tile.Value.X : Tile.Value.O);
        nextMoveX = !nextMoveX;

        Tile.Value winValue = evaluateWinValue();
        if (winValue != null) {
            Tile.Value playerValue = playerGoFirst ? Tile.Value.X : Tile.Value.O;
            playerState = winValue == playerValue ? PlayerState.WIN : PlayerState.LOSS;
        } else {
            playerState = board.isFull() ? PlayerState.DRAW : PlayerState.IN_PROGRESS;
        }
    }

    private Tile.Value evaluateWinValue() {
        for (List<Tile> line : board.getAllLines()) {
            Tile first = line.get(0);
            if (first.isEmpty()) {
                continue;
            }

            if (line.stream().allMatch(t -> t.getValue() == first.getValue())) {
                return first.getValue();
            }
        }

        return null;
    }
}
