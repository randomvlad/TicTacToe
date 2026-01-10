package tictactoe.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import tictactoe.user.dao.AppUserRepository;
import tictactoe.user.dao.model.AppUser;

@Component
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository repository;

    /**
     * @throws UsernameNotFoundException if {@code username} is not found
     */
    @NonNull
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
