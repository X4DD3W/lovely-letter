package hu.xaddew.lovelyletter.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
@Table(name = "games")
public class Game {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String uuid;

  @OneToMany(mappedBy = "game")
  private List<Card> drawDeck;

  @OneToMany(mappedBy = "game")
  private List<Player> playersInGame;

  private String actualPlayer;

  @ElementCollection()
  private List<String> log;

  public Game() {
    this.id = null;
    this.uuid = null;
    this.drawDeck = new LinkedList<>();
    this.playersInGame = new ArrayList<>();
    this.actualPlayer = null;
    this.log = new LinkedList<>();
  }

  public String addLog(String message) {
    String newLog = (this.log.size() + 1) + ". " + message;
    this.log.add(newLog);
    return newLog;
  }
}
