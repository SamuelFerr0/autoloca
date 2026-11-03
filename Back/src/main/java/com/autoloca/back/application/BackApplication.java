package com.autoloca.back.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.autoloca.back")
@EntityScan("com.autoloca.back.model")
@EnableJpaRepositories("com.autoloca.back.repository")
public class BackApplication {
  public static void main(String[] args) {
    SpringApplication.run(BackApplication.class, args);
  }
}
