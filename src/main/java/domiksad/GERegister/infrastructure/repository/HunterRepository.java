package domiksad.GERegister.infrastructure.repository;

import domiksad.GERegister.infrastructure.entity.HunterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HunterRepository extends JpaRepository<HunterEntity, Long> {
}
