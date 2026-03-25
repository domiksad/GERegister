package domiksad.GERegister.objects;

import domiksad.GERegister.infrastructure.entity.HunterEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class HunterEntityTest {
  @Test
  void equalsTest(){
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    HunterEntity h1 = new HunterEntity(id1, "h1");
    HunterEntity h2 = new HunterEntity(id1, "h2");
    HunterEntity h3 = new HunterEntity(id2, "h3");

    assertEquals(h1, h2);
    assertNotEquals(h1, h3);
    assertNotEquals(h2, h3);
  }
}
