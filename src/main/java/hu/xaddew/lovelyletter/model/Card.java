package hu.xaddew.lovelyletter.model;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
  private Long id;

  @ManyToMany(cascade = CascadeType.ALL)
  private List<Game> game;

  @ManyToMany(cascade = CascadeType.ALL)
  private List<Player> inPlayersHand;

  @ManyToMany(cascade = CascadeType.ALL)
  private List<Player> inPlayedArea;

  private String cardName;
  private Integer cardValue;
  private Integer quantity;
  private String description;

  // TODO félrerakás sorrendje!!!
  private Boolean isPutAside;
}
