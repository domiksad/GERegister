package domiksad.GERegister.testContainers;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class testContainerTest {
  @Container
  static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");


}
