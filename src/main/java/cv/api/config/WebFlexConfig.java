package cv.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Configuration
@Slf4j
@PropertySource(value = {"file:config/application.properties"})
public class WebFlexConfig {

    @Autowired
    private Environment environment;

    @Bean
    public WebClient inventoryApi() {
        log.info("inventoryApi : " + environment.getProperty("inventory.url"));
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(config -> config
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(Objects.requireNonNull(environment.getProperty("inventory.url")))
                .build();
    }

    @Bean
    public WebClient accountApi() {
        log.info("account : " + environment.getProperty("account.url"));
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(config -> config
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(Objects.requireNonNull(environment.getProperty("account.url")))
                .build();
    }

    @Bean
    public WebClient userApi() {
        log.info("user api : " + environment.getProperty("user.url"));
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(config -> config
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(Objects.requireNonNull(environment.getProperty("user.url")))
                .build();
    }

    @Bean
    public WebClient reportApi() {
        String reportUrl = environment.getProperty("report.url");
        if (!Objects.isNull(reportUrl)) {
            log.info("report api : " + reportUrl);
            return WebClient.builder()
                    .exchangeStrategies(ExchangeStrategies.builder()
                            .codecs(config -> config
                                    .defaultCodecs()
                                    .maxInMemorySize(16 * 1024 * 1024))
                            .build())
                    .baseUrl(Objects.requireNonNull(reportUrl))
                    .build();
        }
        return WebClient.builder().build();
    }
}
