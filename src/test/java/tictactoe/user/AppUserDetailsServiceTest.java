package tictactoe.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tictactoe.user.entity.AppUser;
import tictactoe.user.entity.AppUserRepository;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    private AppUserDetailsService service;

    @Mock
    private AppUserRepository mockRepository;

    @BeforeEach
    void setUp() {
        service = new AppUserDetailsService(mockRepository);
    }

    @Test
    void loadUserByUsername_ValidUsername_ExpectedUserDetails() {
        AppUser appUser = new AppUser();
        appUser.setUsername("test-username");
        appUser.setPassword("test-password");
        when(mockRepository.findByUsername(anyString())).thenReturn(appUser);

        UserDetails userDetails = service.loadUserByUsername("test-username");

        verify(mockRepository).findByUsername(appUser.getUsername());

        assertThat(userDetails.getUsername()).isEqualTo(appUser.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo(appUser.getPassword());
        assertThat(userDetails.getAuthorities()).hasSize(1);

        GrantedAuthority actualAuthority = userDetails.getAuthorities().iterator().next();
        assertThat(actualAuthority.getAuthority()).isEqualTo("USER");
    }

    @Test
    void loadUserByUsername_InvalidUsername_ThrowException() {
        when(mockRepository.findByUsername(anyString())).thenReturn(null);

        assertThatThrownBy(() -> service.loadUserByUsername("test-username"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Invalid username: test-username");
    }
}
