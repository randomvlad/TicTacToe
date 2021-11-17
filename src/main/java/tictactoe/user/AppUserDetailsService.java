package tictactoe.user;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tictactoe.user.entity.AppUser;
import tictactoe.user.entity.AppUserRepository;

@Component
public class AppUserDetailsService implements UserDetailsService {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private final AppUserRepository repository;

    @Autowired
    public AppUserDetailsService(AppUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = repository.findByUsername(username);
        if (appUser == null) {
            throw new UsernameNotFoundException("Invalid username: " + username);
        }
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("USER");
        return new User(appUser.getUsername(), appUser.getPassword(), authorities);
    }
}
