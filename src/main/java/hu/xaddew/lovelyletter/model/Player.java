package hu.xaddew.lovelyletter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "players")
public class Player {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String uuid;

  private String name;

  @ElementCollection()
  private List<Card> cardsInHand;

  @ElementCollection
  private List<Card> playedCards;

  private Integer numberOfLetters;

  private Boolean isInPlay;

  @JsonIgnore
  @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
  private Game game;

  public Player() {
    this.id = null;
    this.uuid = null;
    this.name = null;
    this.cardsInHand = new ArrayList<>();
    this.playedCards = new LinkedList<>();
    this.numberOfLetters = 0;
    this.isInPlay = true;
  }
}
