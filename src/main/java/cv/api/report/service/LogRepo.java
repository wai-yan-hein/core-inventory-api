package cv.api.report.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

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


}
