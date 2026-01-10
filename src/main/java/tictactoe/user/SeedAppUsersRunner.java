package tictactoe.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tictactoe.user.dao.model.AppUser;
import tictactoe.user.dao.AppUserRepository;

/**
 * Seed H2 database with application users to log in with.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class SeedAppUsersRunner implements CommandLineRunner {

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createUser("rick", "pickle");
        createUser("morty", "pickle");
    }

    private void createUser(String username, String password) {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        repository.save(user);

        log.info("Created user ({}) with username '{}' and password '{}'", user.getId(), username, password);
    }
}
