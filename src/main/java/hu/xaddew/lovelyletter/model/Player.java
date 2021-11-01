package hu.xaddew.lovelyletter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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
@Table(name = "player")
public class Player {

  @Id
  @Column(name = "player_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "uuid")
  private String uuid;

  @Column(name = "name")
  private String name;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name="game_id", nullable = false)
  private Game game;

  @ManyToMany
  @JoinTable(
      name = "player_cards_in_hand",
      joinColumns = @JoinColumn(name = "player_id"),
      inverseJoinColumns = @JoinColumn(name = "card_id"))
  private List<Card> cardsInHand;

  @ManyToMany
  @JoinTable(
      name = "player_played_cards",
      joinColumns = @JoinColumn(name = "player_id"),
      inverseJoinColumns = @JoinColumn(name = "card_id"))
  private List<Card> playedCards;

  @Column(name = "number_of_letters")
  private Integer numberOfLetters;

  @JsonIgnore
  @Column(name = "is_in_play")
  private Boolean isInPlay;

  @JsonIgnore
  @Column(name = "order_number")
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

  public boolean hasOnlyOneCardInHand() {
    return this.cardsInHand.size() == 1;
  }
}
