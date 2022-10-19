package cv.api.repo;

import cv.api.model.PropertyKey;
import cv.api.model.SystemProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

@Slf4j
@Component
public class UserRepo {
    @Autowired
    private WebClient userApi;
    int min = 1;

    public SystemProperty findProperty(String key, String compCode) {
        PropertyKey p = new PropertyKey();
        p.setPropKey(key);
        p.setCompCode(compCode);
        Mono<SystemProperty> result = userApi.post()
                .uri("/user/find-system-property")
                .body(Mono.just(p), PropertyKey.class)
                .retrieve()
                .bodyToMono(SystemProperty.class);
        return result.block(Duration.ofMinutes(min));
    }
}
