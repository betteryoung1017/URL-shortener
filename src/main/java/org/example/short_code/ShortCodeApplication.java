package org.example.short_code;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("mapper")
public class ShortCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortCodeApplication.class, args);
    }

}
