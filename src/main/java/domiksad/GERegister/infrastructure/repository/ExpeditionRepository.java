package domiksad.GERegister.infrastructure.repository;

import domiksad.GERegister.infrastructure.entity.ExpeditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExpeditionRepository extends JpaRepository<ExpeditionEntity, UUID>, JpaSpecificationExecutor<ExpeditionEntity> {
}
