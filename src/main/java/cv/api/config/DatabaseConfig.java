package cv.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"file:config/application.yaml"})
public class DatabaseConfig {
}
