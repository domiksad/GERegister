package domiksad.GERegister.domain.hunter;

import domiksad.GERegister.domain.expedition.Expedition;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Hunter {
  private Long id;

  private String name;
  private Set<Expedition> expeditions = new HashSet<>();

  public void addExpedition(Expedition expedition) {
    expeditions.add(expedition);
  }

  public boolean isInProgress() {
    return expeditions.stream().anyMatch(e -> e.getStatus() == ExpeditionStatus.IN_PROGRESS);
  }
}
