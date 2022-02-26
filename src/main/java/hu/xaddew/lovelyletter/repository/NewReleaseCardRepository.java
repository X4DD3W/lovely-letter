package hu.xaddew.lovelyletter.repository;

import hu.xaddew.lovelyletter.domain.NewReleaseCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewReleaseCardRepository extends JpaRepository<NewReleaseCard, Long> {

}
