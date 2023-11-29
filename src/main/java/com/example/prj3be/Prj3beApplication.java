package com.example.prj3be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//@EnableJpaAuditing -> 중복으로 쓰면 안 됨
@EnableJpaAuditing
@SpringBootApplication
public class Prj3beApplication {

    public static void main(String[] args) {
        SpringApplication.run(Prj3beApplication.class, args);
    }

}
