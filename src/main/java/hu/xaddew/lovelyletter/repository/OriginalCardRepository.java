package hu.xaddew.lovelyletter.repository;

import hu.xaddew.lovelyletter.domain.OriginalCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OriginalCardRepository extends JpaRepository<OriginalCard, Long> {

}
