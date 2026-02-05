package tictactoe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.UseMainMethod;

@SpringBootTest(useMainMethod = UseMainMethod.ALWAYS)
class TicTacToeAppTest {

    /**
     * @see <a href="https://www.baeldung.com/spring-boot-testing-main-class">Testing the Main Class of a Spring Boot Application</a>
     */
    @Test
    void contextLoads_Start_LoadNoErrors() {
    }
}
