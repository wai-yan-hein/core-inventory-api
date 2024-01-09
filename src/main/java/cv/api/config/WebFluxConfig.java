package cv.api.config;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
@Slf4j
public class WebFluxConfig implements WebFluxConfigurer {

    @Override
    public void configureHttpMessageCodecs(@NotNull ServerCodecConfigurer configure) {
        configure.defaultCodecs().maxInMemorySize(5 * 1024 * 1024); // 5 MB
        log.info("configureHttpMessageCodecs configured.");
    }

}
