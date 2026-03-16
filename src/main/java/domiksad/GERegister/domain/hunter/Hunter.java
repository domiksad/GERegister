package domiksad.GERegister.domain.hunter;

import domiksad.GERegister.domain.expedition.Expedition;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Hunter {
    private String name;
    private List<Expedition> expeditions = new ArrayList<>();

    public void addExpedition(Expedition expedition){
        expeditions.add(expedition);
    }

    public boolean isInProgress() {
        return expeditions.stream()
                .anyMatch(e -> e.getStatus() == ExpeditionStatus.IN_PROGRESS);
    }
}
