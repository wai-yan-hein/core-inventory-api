package cv.api;

import cv.api.tray.AppTray;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InventoryApiApplication {
    public static void main(String[] args) {
        System.setProperty("spring.main.lazy-initialization", "true");
        SpringApplication.run(InventoryApiApplication.class, args);
    }
}
