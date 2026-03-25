package domiksad.GERegister.infrastructure.entity;

import domiksad.GERegister.domain.expedition.Difficulty;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
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
  private Instant startDate = null;
  private Instant finishDate = null;

  @ManyToMany private Set<HunterEntity> hunters = new HashSet<>();

  public Set<HunterEntity> getHunters() {
    return Collections.unmodifiableSet(hunters);
  }

  public void addHunter(HunterEntity hunter) {
    this.hunters.add(hunter);
  }

  public void removeHunter(UUID hunterId) {
    this.hunters.removeIf(h -> h.getId().equals(hunterId));
  }
}
