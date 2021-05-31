package hu.xaddew.lovelyletter.repository;

import hu.xaddew.lovelyletter.model.Player;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

  @Query(value = "SELECT * FROM players p WHERE p.uuid = :uuid", nativeQuery = true)
  Player findByUuid(@Param("uuid") String uuid);

}
