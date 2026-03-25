package domiksad.GERegister.domain.expedition;

import domiksad.GERegister.domain.exceptions.ExpeditionException;
import domiksad.GERegister.domain.hunter.Hunter;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import domiksad.GERegister.infrastructure.repository.ExpeditionRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Expedition {
  private UUID id;

  private String name;
  private String description;
  private Difficulty difficulty;
  private ExpeditionStatus status = ExpeditionStatus.CREATED;
  private Instant startDate = null;
  private Instant finishDate = null;
  private Set<Hunter> hunters = new HashSet<>() {};

  public void addHunter(Hunter hunter) {
    if (status != ExpeditionStatus.CREATED)
      throw new ExpeditionException("Expedition must be in CREATED status to add hunters");
    hunters.add(hunter);
  }

  public void removeHunter(Hunter hunter) {
    if (status != ExpeditionStatus.CREATED)
      throw new ExpeditionException("Expedition must be in CREATED status to remove hunters");
    hunters.remove(hunter);
  }

  public void start(boolean isAnyHunterBusy) {
    if (status != ExpeditionStatus.CREATED)
      throw new ExpeditionException("Expedition must be in CREATED status to start");

    if (hunters.isEmpty()) throw new ExpeditionException("Cannot start expedition without hunters");

    if (isAnyHunterBusy)
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
