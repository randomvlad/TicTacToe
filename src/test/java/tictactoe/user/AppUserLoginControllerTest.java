package tictactoe.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(AppUserLoginController.class)
class AppUserLoginControllerTest {

    @Autowired
    MockMvcTester mockMvcTester;

    @Test
    void login_GetRequest_HttpStatusOk() {
        assertThat(mockMvcTester.get().uri("/login"))
                .hasStatusOk()
                .hasViewName("login")
                .model().isEmpty();
    }
}
