package hu.xaddew.lovelyletter;

import java.util.Random;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LovelyLetterApplication {

  public static void main(String[] args) {
    SpringApplication.run(LovelyLetterApplication.class, args);
  }

  @Bean
  public Random getRandom() {
    return new Random();
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

}
