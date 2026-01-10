package tictactoe.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.jspecify.annotations.Nullable;
import tictactoe.game.model.BoardTile;

class BoardUtil {

    private static final int NUMBER_ROWS = 3;
    private static final int NUMBER_COLUMNS = 3;

    public static List<List<String>> createEmpty() {
        List<List<String>> rows = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < NUMBER_ROWS; rowIndex++) {
            List<String> row = new ArrayList<>();
            for (int columnIndex = 0; columnIndex < NUMBER_COLUMNS; columnIndex++) {
                row.add(BoardTile.EMPTY.getValue());

            }
            rows.add(row);
        }

        return rows;
    }

    /**
     * @return tile string ID in format "{rowId}-{columnId}". Example: "0-1" (row 0, column 1).
     * Indices are 0-based. {@code null} if all tiles are taken.
     */
    @Nullable
    public static String getRandomAvailableTile(List<List<String>> rows) {
        List<String> available = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            List<String> row = rows.get(rowIndex);

            for (int columnIndex = 0; columnIndex < rows.size(); columnIndex++) {
                String tileValue = row.get(columnIndex);
                if (tileValue.isEmpty()) {
                    available.add(rowIndex + "-" + columnIndex);
                }
            }
        }

        if (available.isEmpty()) {
            return null;
        }

        int randomNum = new Random().nextInt(available.size());
        return available.get(randomNum);

    }

    public static List<List<String>> getAllLines(List<List<String>> rows) {
        List<List<String>> lines = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < NUMBER_ROWS; rowIndex++) {
            lines.add(rows.get(rowIndex));
        }

        for (int columnIndex = 0; columnIndex < NUMBER_COLUMNS; columnIndex++) {
            List<String> columnLine = new ArrayList<>();
            for (List<String> row : rows) {
                columnLine.add(row.get(columnIndex));
            }
            lines.add(columnLine);
        }

        List<String> diagonal1 = List.of(rows.get(0).get(0), rows.get(1).get(1), rows.get(2).get(2));
        lines.add(diagonal1);

        List<String> diagonal2 = List.of(rows.get(0).get(2), rows.get(1).get(1), rows.get(2).get(0));
        lines.add(diagonal2);

        return lines;
    }
}
