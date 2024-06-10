package cv.api.repo;


import cv.api.common.Util1;
import cv.api.report.model.CompanyInfoDto;
import cv.api.security.AuthenticationRequest;
import cv.api.security.AuthenticationResponse;
import cv.api.user.SystemPropertyDto;
import cv.api.user.SystemPropertyKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@PropertySource(value = {"file:config/application.yaml"})
@Slf4j
public class UserRepo {
    private String token;
    private final Environment environment;
    private final ReactorClientHttpConnector reactorClientHttpConnector;

    public void createWebClient() {
        if (token == null) {
            token = getToken();
        }
        //log.info("token : {}", token);

    }

    private WebClient userApi(String token) {
        String url = environment.getRequiredProperty("cloud.user.url");
        WebClient.Builder builder = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configure -> configure
                                .defaultCodecs()
                                .maxInMemorySize(100 * 1024 * 1024))
                        .build())
                .clientConnector(reactorClientHttpConnector)
                .baseUrl(url);

        if (token != null) {
            builder = builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        return builder.build();
    }


    private String getToken() {
        String programName = "core-inventory-api";
        var auth = AuthenticationRequest.builder()
                .programName(programName)
                .password(Util1.getPassword())
                .build();
        return userApi(null).post()
                .uri("/auth/getToken")
                .body(Mono.just(auth), AuthenticationRequest.class)
                .retrieve()
                .bodyToMono(AuthenticationResponse.class)
                .map(AuthenticationResponse::getAccessToken) // Extract and return the access token
                .onErrorResume(throwable -> {
                    //log.error("authenticate : {}", throwable.getMessage());
                    return Mono.empty();
                }).block();
    }


    public Mono<List<CompanyInfoDto>> getCompanySync() {
        return userApi(token).get()
                .uri(builder -> builder.path("/user/getCompanySync")
                        .build())
                .retrieve()
                .bodyToFlux(CompanyInfoDto.class)
                .collectList();
    }

    public Mono<SystemPropertyDto> findSystemProperty(SystemPropertyKey key) {
        return userApi(token).post()
                .uri("/user/findSystemProperty")
                .body(Mono.just(key), SystemPropertyDto.class)
                .retrieve()
                .bodyToMono(SystemPropertyDto.class);
    }


}
