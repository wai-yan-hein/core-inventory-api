package cv.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Slf4j
@Configuration
public class WebFluxConfiguration implements WebFluxConfigurer {
    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configure) {
        configure.defaultCodecs().maxInMemorySize(5 * 1024 * 1024);//5MB
        log.info("configureHttpMessageCodecs : 5MB");
    }
}