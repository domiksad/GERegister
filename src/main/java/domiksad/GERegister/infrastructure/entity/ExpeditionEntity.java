package domiksad.GERegister.infrastructure.entity;

import domiksad.GERegister.domain.expedition.Difficulty;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpeditionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  private String name;
  private String description;
  private Difficulty difficulty;
  private ExpeditionStatus status = ExpeditionStatus.CREATED;
  private Instant startDate;
  private Instant finishDate;

  @ManyToMany private Set<HunterEntity> hunters;
}
