package domiksad.GERegister.domain.expedition;

import domiksad.GERegister.domain.exceptions.ExpeditionException;
import domiksad.GERegister.domain.hunter.Hunter;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Expedition {
  private Long id;

  private String name;
  private String description;
  private Difficulty difficulty;
  private ExpeditionStatus status = ExpeditionStatus.CREATED;
  private Instant startDate;
  private Instant finishDate;
  private Set<Hunter> hunters = new HashSet<>() {};

  public void addHunter(Hunter hunter) {
    if (status != ExpeditionStatus.CREATED)
      throw new ExpeditionException("Expedition must be in CREATED status to add hunters");
    if (!hunters.contains(hunter)) {
      hunters.add(hunter);
      hunter.addExpedition(this);
    }
  }

  public void removeHunter(Hunter hunter) {
    if (status != ExpeditionStatus.CREATED)
      throw new ExpeditionException("Expedition must be in CREATED status to remove hunters");
    hunters.remove(hunter);
  }

  public void start() {
    if (status != ExpeditionStatus.CREATED)
      throw new ExpeditionException("Expedition must be in CREATED status to start");

    if (hunters.isEmpty()) throw new ExpeditionException("Cannot start expedition without hunters");

    if (hunters.stream().anyMatch(Hunter::isInProgress))
      throw new ExpeditionException("One or more hunters are already in another expedition");

    status = ExpeditionStatus.IN_PROGRESS;
    startDate = Instant.now();
  }

  public void finish() {
    if (status == ExpeditionStatus.IN_PROGRESS) {
      status = ExpeditionStatus.FINISHED;
      finishDate = Instant.now();
    } else throw new ExpeditionException("Expedition must be in IN_PROGRESS status to finish");
  }
}
