package cv.api.config;

import cv.api.common.Util1;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@RequiredArgsConstructor
@Configuration
public class NettyServerConfig {
    private final Environment environment;

    @Bean
    public WebServerFactoryCustomizer<NettyReactiveWebServerFactory> httpsCustomizer() {
        return factory -> {
            Ssl ssl = new Ssl();
            ssl.setEnabled(true);
            // Configure your keystore, key alias, and passwords
            ssl.setKeyStore("classpath:corevalue.jks");
            ssl.setKeyAlias("corevalue");
            ssl.setKeyPassword("corevalue");
            ssl.setKeyStorePassword("corevalue");
            factory.setSsl(ssl);
            // Set your HTTPS port
            factory.setPort(Util1.getInteger(environment.getProperty("server.port")));
        };
    }
}
