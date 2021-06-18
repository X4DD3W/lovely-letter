package hu.xaddew.lovelyletter.repository;

import hu.xaddew.lovelyletter.model.CustomCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomCardRepository extends JpaRepository<CustomCard, Long> {

}
