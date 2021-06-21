package hu.xaddew.lovelyletter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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

  @JsonIgnore
  @ManyToOne(cascade = CascadeType.ALL)
  private Game game;

  @ManyToMany
  private List<Card> cardsInHand;

  @ManyToMany
  private List<Card> playedCards;

  private Integer numberOfLetters;

  @JsonIgnore
  private Boolean isInPlay;

  @JsonIgnore
  private Integer orderNumber;

  public Player() {
    this.id = null;
    this.uuid = null;
    this.name = null;
    this.cardsInHand = new ArrayList<>();
    this.playedCards = new LinkedList<>();
    this.numberOfLetters = 0;
    this.isInPlay = true;
    this.orderNumber = null;
  }

  public void discard(Card card) {
    this.cardsInHand.remove(card);
    this.playedCards.add(card);
  }

  public Card lastPlayedCard() {
    if (!this.playedCards.isEmpty()) {
      return this.playedCards.get(this.playedCards.size() - 1);
    } else {
      return null;
    }
  }

  public Card cardInHand() {
    return this.getCardsInHand().get(0);
  }

  public void addOneLetter() {
    this.numberOfLetters++;
  }
}
