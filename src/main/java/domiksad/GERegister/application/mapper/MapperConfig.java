package domiksad.GERegister.application.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
  @Bean
  public HunterMapper hunterMapper(){
    return Mappers.getMapper(HunterMapper.class);
  }

  @Bean
  public ExpeditionMapper expeditionMapper(){
    return Mappers.getMapper(ExpeditionMapper.class);
  }
}
