package tictactoe.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tictactoe.user.dao.model.AppUser;

@Transactional(readOnly = true)
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByUsername(String name);
}
