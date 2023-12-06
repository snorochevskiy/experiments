package me.snorochevskiy.springfactories.demo.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApp {
    public static void main(String[] args) {
        SpringApplication springApp = new SpringApplication(DemoApp.class);
        springApp.run(args);
    }
}
