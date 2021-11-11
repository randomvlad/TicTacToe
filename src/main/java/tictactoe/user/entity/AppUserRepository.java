package tictactoe.user.entity;

import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface AppUserRepository extends Repository<AppUser, Long> {

    AppUser save(AppUser user);

    AppUser findByUsername(String name);
}
