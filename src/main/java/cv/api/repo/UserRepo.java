package cv.api.repo;

import cv.api.user.PropertyKey;
import cv.api.user.SysProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserRepo {
    private final WebClient userApi;

    public Mono<String> findSystemProperty(PropertyKey key) {
        return userApi.post()
                .uri("/user/findSystemProperty")
                .body(Mono.just(key), PropertyKey.class)
                .retrieve()
                .bodyToMono(SysProperty.class)
                .map(SysProperty::getPropValue);
    }
}
