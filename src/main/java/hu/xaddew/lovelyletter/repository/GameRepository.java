package hu.xaddew.lovelyletter.repository;

import hu.xaddew.lovelyletter.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

  Game findByUuid(String uuid);

  //   @Query("select d from Document d left join fetch CaseEntity c on d.caseId = c.id "
  //      + " where d.documentStatus = :status "
  //      + "   and (:caseUniqueId is null or :caseUniqueId = '' or c.caseUniqueId like concat('%',:caseUniqueId,'%'))"
  //      + "   and (:owner is null or :owner = '' or d.createdBy = :owner)")
  //  Page<Document> findByStatusAndFilterParams(DocumentStatus status, String caseUniqueId, String owner,
  //      Pageable pageable);

  @Query(value = "SELECT * FROM games g "
      + "LEFT JOIN games_players_in_game gp ON g.id = gp.game_id "
      + "LEFT JOIN games_players_out_of_game go ON g.id = go.game_id "
      + "LEFT JOIN players p ON p.id = gp.players_in_game_id "
      + "WHERE p.uuid = :playerUuid", nativeQuery = true)
  // TODO itt tartok, nem tud Game-et visszaadni normálisan
  Game findGameByPlayerUuid(@Param("playerUuid") String playerUuid);

}
