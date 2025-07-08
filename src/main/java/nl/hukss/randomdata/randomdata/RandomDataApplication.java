package nl.hukss.randomdata.randomdata;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("nl.hukss.randomdata.randomdata.mapper")
public class RandomDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(RandomDataApplication.class, args);
    }

}
