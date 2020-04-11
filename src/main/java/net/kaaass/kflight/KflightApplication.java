package net.kaaass.kflight;

import net.kaaass.kflight.eventhandle.EventBus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KflightApplication {

    public static final EventBus EVENT_BUS = new EventBus();

    public static void main(String[] args) {
        SpringApplication.run(KflightApplication.class, args);
    }
}
