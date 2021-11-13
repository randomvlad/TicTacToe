package tictactoe.game.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tictactoe.user.entity.AppUser;

@Repository
@Transactional(readOnly = true)
public interface GameRepository extends JpaRepository<Game, Long> {

    Game findFirstByAppUserOrderByIdDesc(AppUser appUser);

    @Modifying
    @Transactional
    @Query("DELETE FROM Game WHERE appUser = :appUser")
    void deleteUserGames(@Param("appUser") AppUser appUser);

}
