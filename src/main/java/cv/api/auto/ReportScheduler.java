package cv.api.auto;

import cv.api.inv.entity.VStockBalance;
import cv.api.inv.service.ReportService;
import cv.api.model.Response;
import cv.api.model.StockBalance;
import cv.api.model.StockBalanceKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@PropertySource(value = {"file:config/application.properties"})
@Slf4j
public class ReportScheduler {
    @Autowired
    private ReportService reportService;
    @Autowired
    private Environment environment;
    @Autowired
    private WebClient reportApi;

    @Scheduled(fixedRate = 60 * 60 * 1000)
    private void uploadToRedisServer() {
        boolean status = Objects.isNull(environment.getProperty("report.url"));
        if (!status) {
            uploadStockBalance();
        }
    }

    private void uploadStockBalance() {
        try {
            String compCode = environment.getProperty("report.company");
            List<StockBalance> balances = new ArrayList<>();
            if (!Objects.isNull(compCode)) {
                List<VStockBalance> balanceList = reportService.getStockBalance(
                        "-", "-", "-",
                        "-", true, true, true, true, "-", 1, 0);
                for (VStockBalance b : balanceList) {
                    StockBalanceKey key = new StockBalanceKey();
                    key.setStockCode(b.getStockCode());
                    key.setCompCode(compCode);
                    key.setLocCode(b.getLocCode());
                    StockBalance balance = new StockBalance();
                    balance.setKey(key);
                    balance.setUserCode(b.getUserCode());
                    balance.setStockName(b.getStockName());
                    balance.setLocName(b.getLocationName());
                    balance.setBalance(b.getUnitName());
                    balances.add(balance);
                }
                if (!balances.isEmpty()) {
                    Mono<Response> result = reportApi.post()
                            .uri("/api/save-stock-balance")
                            .body(Mono.just(balances), List.class)
                            .retrieve()
                            .bodyToMono(Response.class);
                    result.subscribe(response -> {
                        log.info(response.getMessage());
                    }, err -> {
                        log.error(String.format("uploadStockBalance: %s", err.getLocalizedMessage()));
                    });
                }
            }
        } catch (Exception e) {
            log.error("uploadStockBalance: " + e.getMessage());
        }
    }
}
