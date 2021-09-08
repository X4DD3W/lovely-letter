package hu.xaddew.lovelyletter.repository;

import hu.xaddew.lovelyletter.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

  Player findByUuid(String uuid);
}
