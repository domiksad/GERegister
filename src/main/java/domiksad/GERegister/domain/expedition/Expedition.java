package domiksad.GERegister.domain.expedition;

import domiksad.GERegister.domain.exceptions.ExpeditionException;
import domiksad.GERegister.domain.hunter.Hunter;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Expedition {
    private String name;
    private String description;
    private Difficulty difficulty;
    private ExpeditionStatus status;
    private Instant startDate;
    private Instant finishDate;
    private List<Hunter> hunters = new ArrayList<>();

    public void addHunter(Hunter hunter) {
        if (status != ExpeditionStatus.CREATED)
            throw new ExpeditionException("Expedition must be in CREATED status to add hunters");
        if (!hunters.contains(hunter)) {
            hunters.add(hunter);
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

        if(hunters.isEmpty())
            throw new ExpeditionException("Cannot start expedition without hunters");

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
