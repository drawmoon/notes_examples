package io.github.drawmoon.analyticalretrieverexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(value = {"io.github.drawmoon.analyticalretrieverexample"})
@EnableCaching
public class ApplicationStarter {
  public static void main(String[] args) {
    System.setProperty("spring.cloud.bootstrap.enabled", "true");
    System.setProperty("spring.profiles.active", "local");
    SpringApplication.run(ApplicationStarter.class, args);
  }
}
