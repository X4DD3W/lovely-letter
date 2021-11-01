package hu.xaddew.lovelyletter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "card")
public class Card {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "card_id")
  @JsonIgnore
  private Long id;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name="game_id", nullable = false)
  private Game game;

  @JsonIgnore
  @ManyToMany(mappedBy = "cardsInHand")
  private List<Player> inPlayersHand;

  @JsonIgnore
  @ManyToMany(mappedBy = "playedCards")
  private List<Player> inPlayedArea;

  @Column(name = "name")
  private String name;

  @Column(name = "name_english")
  private String nameEnglish;

  @Column(name = "value")
  private Integer value;

  @Column(name = "quantity")
  private Integer quantity;

  @Column(name = "description")
  private String description;

  @JsonIgnore
  @Column(name = "is_put_aside")
  private Boolean isPutAside;

  @JsonIgnore
  @Column(name = "is_2p_public")
  private Boolean is2PlayerPublic;

  @JsonIgnore
  @Column(name = "is_at_a_player")
  private Boolean isAtAPlayer;

  public Card(String name) {
    this.name = name;
  }

  public Card(String name, Integer value) {
    this.name = name;
    this.value = value;
  }
}
