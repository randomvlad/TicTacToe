package tictactoe.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import tictactoe.user.dao.model.AppUser;
import tictactoe.user.dao.AppUserRepository;

@ExtendWith(MockitoExtension.class)
class SeedAppUsersRunnerTest {

    @InjectMocks
    private SeedAppUsersRunner dbLoader;

    @Mock
    private AppUserRepository mockRepository;

    @Mock
    private PasswordEncoder mockEncoder;

    @Test
    void run_NoArgs_SaveTwoExpectedUsers() {
        // given
        String[] noArgs = {};
        when(mockEncoder.encode(any())).thenReturn(Value.PASSWORD_ENCODED);

        // when
        dbLoader.run(noArgs);

        // then
        verify(mockEncoder, times(2)).encode(Value.PASSWORD);
        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(mockRepository, times(2)).save(captor.capture());
        verifyNoMoreInteractions(mockRepository, mockEncoder);

        List<AppUser> actualUsers = captor.getAllValues();
        AppUser actualUser1 = actualUsers.get(0);
        assertThat(actualUser1.getUsername()).isEqualTo(Value.USER_1_NAME);
        assertThat(actualUser1.getPassword()).isEqualTo(Value.PASSWORD_ENCODED);

        AppUser actualUser2 = actualUsers.get(1);
        assertThat(actualUser2.getUsername()).isEqualTo(Value.USER_2_NAME);
        assertThat(actualUser2.getPassword()).isEqualTo(Value.PASSWORD_ENCODED);
    }

    private static class Value {
        static final String USER_1_NAME = "rick";
        static final String USER_2_NAME = "morty";
        static final String PASSWORD = "pickle";
        static final String PASSWORD_ENCODED = "encoded-pass";
    }
}
