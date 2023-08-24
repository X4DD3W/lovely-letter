package hu.xaddew.lovelyletter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "custom_card")
public class CustomCard {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  private Long id;

  private String cardName;
  private String cardNameEnglish;
  private Integer cardValue;
  private Integer quantity;

  @Column(name = "description_hu")
  private String description;

  @Column(name = "description_en")
  private String descriptionEnglish;

  @JsonIgnore
  private Boolean isPutAside;

  @JsonIgnore
  @Column(name = "is_2p_public")
  private Boolean is2PlayerPublic;

  @JsonIgnore
  @Column(name = "is_at_a_player")
  private Boolean isAtAPlayer;
}
