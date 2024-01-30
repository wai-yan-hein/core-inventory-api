package cv.api.repo;

import cv.api.dms.TraderDMSDto;
import cv.api.service.TraderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class DMSRepo {
    private final TraderService traderService;
    private final WebClient dmsApi;
    private final String ACK = "ACK";


    public Flux<TraderDMSDto> getUpdateTrader(String updatedDate) {
        log.info("getUpdateTrader : " + updatedDate);
        return dmsApi.get()
                .uri(builder -> builder.path("/trader/getUpdateTrader")
                        .queryParam("updatedDate", updatedDate)
                        .build())
                .retrieve()
                .bodyToFlux(TraderDMSDto.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }
}
