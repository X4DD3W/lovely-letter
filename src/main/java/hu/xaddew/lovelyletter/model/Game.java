package hu.xaddew.lovelyletter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "games")
@EntityListeners(AuditingEntityListener.class)
public class Game {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String uuid;

  @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Card> drawDeck;

  @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Player> playersInGame;

  private String actualPlayer;

  @ElementCollection()
  private List<String> log;

  @ElementCollection()
  private List<String> hiddenLog;

  private Boolean isGameOver;

  private Boolean is2019Version;

  @JsonIgnore
  private Boolean isTurnOfChancellorActive;

  @Column(name = "create_date", insertable = false, updatable = false)
  @CreatedDate
  private LocalDateTime createDate;

  @Column(name = "modify_date")
  @LastModifiedDate
  private LocalDateTime modifyDate;

  public Game() {
    this.id = null;
    this.uuid = null;
    this.drawDeck = new LinkedList<>();
    this.playersInGame = new ArrayList<>();
    this.actualPlayer = null;
    this.log = new LinkedList<>();
    this.hiddenLog = new LinkedList<>();
    this.isGameOver = false;
    this.is2019Version = false;
    this.isTurnOfChancellorActive = false;
  }

  public String addLog(String message) {
    String newLog = (this.log.size() + 1) + ". " + message;
    this.log.add(newLog);
    return newLog;
  }

  public String getLastLog() {
    return this.log.isEmpty() ? "" : this.log.get(this.log.size() - 1);
  }

  public void addHiddenLog(String message) {
    String newLog = (this.hiddenLog.size() + 1) + ". " + message;
    this.hiddenLog.add(newLog);
  }

  public Card getPutAsideCard() {
    return this.drawDeck.stream().filter(Card::getIsPutAside).findFirst().orElse(null);
  }

  public List<Card> getPublicCards() {
    return this.drawDeck.stream().filter(Card::getIs2PlayerPublic).collect(Collectors.toList());
  }

  public List<Card> getAvailableCards() {
    return this.getDrawDeck().stream()
        .filter(card -> !card.getIsPutAside())
        .filter(card -> !card.getIs2PlayerPublic())
        .filter(card -> !card.getIsAtAPlayer())
        .collect(Collectors.toList());
  }

  public List<Player> getActivePlayers() {
    return this.getPlayersInGame().stream()
        .filter(Player::getIsInPlay)
        .collect(Collectors.toList());
  }

  public boolean isTurnOfChancellorActive() {
    return this.isTurnOfChancellorActive;
  }

  public boolean is2019Version() {
    return this.is2019Version;
  }
}
