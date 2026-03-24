package domiksad.GERegister.infrastructure.repository;

import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import domiksad.GERegister.infrastructure.entity.ExpeditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpeditionRepository extends JpaRepository<ExpeditionEntity, UUID>, JpaSpecificationExecutor<ExpeditionEntity> {
  boolean existsByHuntersIdAndStatus(UUID hunterId, ExpeditionStatus status);
  List<ExpeditionEntity> findAllByHuntersId(UUID hunterId);

  boolean existsByIdAndHuntersId(UUID expeditionId, UUID hunterId);
}
