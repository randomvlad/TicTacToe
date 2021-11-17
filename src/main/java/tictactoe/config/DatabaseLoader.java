package tictactoe.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tictactoe.user.AppUserDetailsService;
import tictactoe.user.entity.AppUser;
import tictactoe.user.entity.AppUserRepository;

@Component
public class DatabaseLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseLoader.class);

    private final AppUserRepository repository;

    @Autowired
    public DatabaseLoader(AppUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        createUser("rick", "pickle");
        createUser("morty", "pickle");
    }

    private void createUser(String username, String password) {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(AppUserDetailsService.PASSWORD_ENCODER.encode(password));

        repository.save(user);

        logger.info("Created user ({}) with username '{}' and password '{}'", user.getId(), username, password);
    }
}
