package domiksad.GERegister.infrastructure.repository;

import domiksad.GERegister.infrastructure.entity.HunterEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HunterRepository extends JpaRepository<HunterEntity, UUID> {}
