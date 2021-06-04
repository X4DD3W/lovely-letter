package hu.xaddew.lovelyletter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cards")
public class Card {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  private Long id;

  @JsonIgnore
  @ManyToOne(cascade = CascadeType.ALL)
  private Game game;

  @JsonIgnore
  @ManyToMany(cascade = CascadeType.ALL)
  private List<Player> inPlayersHand;

  @JsonIgnore
  @ManyToMany(cascade = CascadeType.ALL)
  private List<Player> inPlayedArea;

  private String cardName;
  private Integer cardValue;
  private Integer quantity;
  private String description;

  // TODO félrerakás sorrendje (putAsideOrder?)
  @JsonIgnore
  private Boolean isPutAside;

  @JsonIgnore
  @Column(name = "is_at_a_player")
  private Boolean isAtAPlayer;
}
