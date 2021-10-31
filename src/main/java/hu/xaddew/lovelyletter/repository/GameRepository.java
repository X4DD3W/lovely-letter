package hu.xaddew.lovelyletter.repository;

import hu.xaddew.lovelyletter.model.Game;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

  Game findByUuid(String uuid);

  @Query(value = "SELECT * FROM game g "
      + "LEFT JOIN player p ON p.game_id = g.id "
      + "WHERE p.uuid = :playerUuid", nativeQuery = true)
  Game findGameByPlayerUuid(@Param("playerUuid") String playerUuid);

  List<Game> findAllByIsGameOverFalseAndModifyDateGreaterThan(LocalDateTime modifyDate);

  void deleteAllByIsGameOverTrueAndModifyDateGreaterThan(LocalDateTime modifyDate);
}
