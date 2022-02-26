package hu.xaddew.lovelyletter.repository;

import hu.xaddew.lovelyletter.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

}
