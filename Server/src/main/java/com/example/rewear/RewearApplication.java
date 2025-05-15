package com.example.rewear;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RewearApplication {

    public static void main(String[] args) {
        SpringApplication.run(RewearApplication.class, args);
    }

}
