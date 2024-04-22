package cv.api.repo;


import cv.api.report.model.Income;
import cv.api.report.model.StockValueDto;
import cv.api.report.model.TopPurchase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LogRepo {
    private WebClient logApi;

    public void createWebClient(String url) {
        if (this.logApi == null) {
            this.logApi = WebClient.builder()
                    .exchangeStrategies(ExchangeStrategies.builder()
                            .codecs(configure -> configure
                                    .defaultCodecs()
                                    .maxInMemorySize(100 * 1024 * 1024))
                            .build())
                    .baseUrl(url)
                    .build();
        }
    }

    public Mono<Boolean> stockValue(List<StockValueDto> list) {
        return logApi.post()
                .uri("/report/stockValue")
                .body(Mono.just(list), List.class)
                .retrieve()
                .bodyToMono(Boolean.class);

    }

    public Mono<Boolean> topPurchase(List<TopPurchase> list) {
        return logApi.post()
                .uri("/report/topPurchase")
                .body(Mono.just(list), List.class)
                .retrieve()
                .bodyToMono(Boolean.class);

    }
    public Mono<Boolean> income(List<Income> list) {
        return logApi.post()
                .uri("/report/income")
                .body(Mono.just(list), List.class)
                .retrieve()
                .bodyToMono(Boolean.class);

    }


}
