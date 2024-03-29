package cv.api.auto;

import cv.api.repo.DMSRepo;
import cv.api.service.TraderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutoDownload {
    private boolean syncing = false;
    private final TraderService traderService;
    private final DMSRepo dmsRepo;

    @Scheduled(fixedRate = 5 * 60 * 1000)
    private void autoDownload() {
        if (!syncing) {
            log.info("autoDownload start.");
            downloadTrader();
            syncing = true;
        }
    }

    private void downloadTrader() {
        traderService.getDMSMaxDate().flatMapMany(dmsRepo::getUpdateTrader)
                .flatMap(trader -> traderService.saveTrader(trader.toTraderInv()))
                .subscribe();
    }
}
