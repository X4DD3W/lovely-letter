package hu.xaddew.lovelyletter.repository;

import hu.xaddew.lovelyletter.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

  Game findByUuid(String uuid);

  @Query(value = "SELECT * FROM games g "
      + "LEFT JOIN games_players_in_game gp ON g.id = gp.game_id "
      + "LEFT JOIN games_players_out_of_game go ON g.id = go.game_id "
      + "LEFT JOIN players p ON p.id = gp.players_in_game_id "
      + "WHERE p.uuid = :playerUuid", nativeQuery = true)
  Game findGameByPlayerUuid(@Param("playerUuid") String playerUuid);

}
