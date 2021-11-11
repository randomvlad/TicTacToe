package tictactoe.game;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class BoardUtilTest {

    @Test
    void createEmpty_Call_Empty3x3Board() {
        List<List<String>> rows = BoardUtil.createEmpty();
        assertThat(rows).hasSize(3);
        assertThat(rows.get(0)).hasSize(3).containsExactly("", "", "");
        assertThat(rows.get(1)).hasSize(3).containsExactly("", "", "");
        assertThat(rows.get(2)).hasSize(3).containsExactly("", "", "");
    }

    @Test
    void getRandomAvailableTile_OnlyCenterTileAvailable_PickCenterTile() {
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("x", "o", "x"),
                Arrays.asList("o", "", "o"),
                Arrays.asList("x", "o", "x")
        );//@formatter:on

        String tileId = BoardUtil.getRandomAvailableTile(rows);
        assertThat(tileId).isEqualTo("1-1");
    }

    @Test
    void getRandomAvailableTile_AllTilesTaken_ReturnNull() {
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("x", "o", "x"),
                Arrays.asList("o", "x", "o"),
                Arrays.asList("x", "o", "x")
        );//@formatter:on

        String tileId = BoardUtil.getRandomAvailableTile(rows);
        assertThat(tileId).isNull();
    }

    @Test
    void getAllLines_Call_ExpectedEightLines() {
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("1", "2", "3"),
                Arrays.asList("4", "5", "6"),
                Arrays.asList("7", "8", "9")
        );//@formatter:on

        List<List<String>> lines = BoardUtil.getAllLines(rows);
        assertThat(lines).hasSize(8);

        // rows
        assertThat(lines.get(0)).containsExactly("1", "2", "3");
        assertThat(lines.get(1)).containsExactly("4", "5", "6");
        assertThat(lines.get(2)).containsExactly("7", "8", "9");

        // columns
        assertThat(lines.get(3)).containsExactly("1", "4", "7");
        assertThat(lines.get(4)).containsExactly("2", "5", "8");
        assertThat(lines.get(5)).containsExactly("3", "6", "9");

        // diagonals
        assertThat(lines.get(6)).containsExactly("1", "5", "9");
        assertThat(lines.get(7)).containsExactly("3", "5", "7");
    }
}
