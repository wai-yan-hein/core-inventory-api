package cv.api.config;

import cv.api.common.Util1;
import cv.api.security.AuthenticationRequest;
import cv.api.security.AuthenticationResponse;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import javax.net.ssl.SSLException;
import java.time.Duration;
import java.util.Objects;

@Configuration
@Slf4j
@RequiredArgsConstructor
@PropertySource(value = {"file:config/application.properties"})
public class WebClientConfig {

    private final Environment environment;

    @Bean
    public WebClient dmsApi() {
        log.info("dms api : {}", environment.getProperty("dms.url"));
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(config -> config
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(Objects.requireNonNull(environment.getProperty("dms.url")))
                .clientConnector(reactorClientHttpConnector())
                .build();
    }

    @Bean
    public WebClient accountApi() {
        log.info("account : {}", environment.getProperty("account.url"));
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(config -> config
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(Objects.requireNonNull(environment.getProperty("account.url")))
                .clientConnector(reactorClientHttpConnector())
                .build();
    }


    @Bean
    public ConnectionProvider connectionProvider() {
        return ConnectionProvider.builder("custom-provider")
                .maxConnections(100) // maximum number of connections
                .maxIdleTime(Duration.ofSeconds(10)) // maximum idle time
                .maxLifeTime(Duration.ofSeconds(60)) // maximum lifetime
                .pendingAcquireTimeout(Duration.ofSeconds(30)) // pending acquire timeout
                .evictInBackground(Duration.ofSeconds(30)) // eviction interval
                .build();
    }

    @Bean
    public HttpClient httpClient() {
        try {
            SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            return HttpClient.create(connectionProvider())
                    .secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
        } catch (SSLException ex) {
            log.error("Error creating HttpClient: {}", ex.getMessage());
        }
        return HttpClient.create(); // Return a default HttpClient if an error occurs    }
    }

    @Bean
    public ReactorClientHttpConnector reactorClientHttpConnector() {
        return new ReactorClientHttpConnector(httpClient());
    }

}
