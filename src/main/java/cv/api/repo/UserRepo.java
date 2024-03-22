package cv.api.repo;


import cv.api.common.Util1;
import cv.api.report.model.CompanyInfoDto;
import cv.api.user.SystemPropertyDto;
import cv.api.user.SystemPropertyKey;
import cv.api.security.AuthenticationRequest;
import cv.api.security.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@PropertySource(value = {"file:config/application.properties"})
@Slf4j
public class UserRepo {
    private WebClient userApi;
    private String token;
    private final Environment environment;

    public void createWebClient() {
        if (token == null) {
            String url = environment.getRequiredProperty("user.url");
            token = getToken(url);
            log.info("token : " + token);
            this.userApi = WebClient.builder()
                    .exchangeStrategies(ExchangeStrategies.builder()
                            .codecs(configure -> configure
                                    .defaultCodecs()
                                    .maxInMemorySize(100 * 1024 * 1024))
                            .build())
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .baseUrl(url)
                    .build();
        }
    }

    public String getToken(String url) {
        return authenticate(url);
    }

    private String authenticate(String url) {
        String programName = "core-inventory-api";
        var auth = AuthenticationRequest.builder()
                .programName(programName)
                .password(Util1.getPassword())
                .build();
        WebClient client = WebClient.builder()
                .baseUrl(Objects.requireNonNull(url))
                .build();
        return client.post()
                .uri("/auth/getToken")
                .body(Mono.just(auth), AuthenticationRequest.class)
                .retrieve()
                .bodyToMono(AuthenticationResponse.class)
                .map(AuthenticationResponse::getAccessToken) // Extract and return the access token
                .onErrorResume(throwable -> {
                    log.error("authenticate : " + throwable.getMessage());
                    return Mono.empty();
                }).block();
    }


    public Mono<List<CompanyInfoDto>> getCompanySync() {
        return userApi.get()
                .uri(builder -> builder.path("/user/getCompanySync")
                        .build())
                .retrieve()
                .bodyToFlux(CompanyInfoDto.class)
                .collectList();
    }

    public Mono<SystemPropertyDto> findSystemProperty(SystemPropertyKey key) {
        createWebClient();
        return userApi.post()
                .uri("/user/findSystemProperty")
                .body(Mono.just(key), SystemPropertyDto.class)
                .retrieve()
                .bodyToMono(SystemPropertyDto.class);
    }


}
