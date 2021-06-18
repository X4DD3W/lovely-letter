package hu.xaddew.lovelyletter.repository;

import hu.xaddew.lovelyletter.model.NewReleaseCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewReleaseCardRepository extends JpaRepository<NewReleaseCard, Long> {

}
