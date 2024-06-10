package cv.api.auto;

import cv.api.common.Util1;
import cv.api.repo.AccountRepo;
import cv.api.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
@PropertySource("file:config/application.yaml")
public class AutoUpload {
    @Value("${sync.date}")
    private String syncDate;
    private final SaleHisService saleHisService;
    private final PurHisService purHisService;
    private final RetInService retInService;
    private final RetOutService retOutService;
    private final TraderService traderService;
    private final PaymentHisService paymentHisService;
    private final LabourPaymentService labourPaymentService;
    private boolean syncing = false;
    private final AccountRepo accountRepo;
    private final Environment environment;

    @Scheduled(fixedRate = 5 * 60 * 1000)
    private void autoUpload() {
        Util1.SYNC_DATE = environment.getProperty("sync.date");
        if (Util1.getBoolean(environment.getProperty("integration"))) {
            if (!syncing) {
                syncing = true;
                uploadTrader();
                uploadSaleVoucher();
                uploadPurchaseVoucher();
                uploadReturnInVoucher();
                uploadReturnOutVoucher();
                uploadPayment();
                uploadLabourPayment();
                syncing = false;
            }
        }
    }

    private void uploadTrader() {
        traderService.unUploadTrader()
                .doOnNext(accountRepo::sendTrader)
                .delayElements(Duration.ofMillis(500))
                .subscribe();
    }


    private void uploadSaleVoucher() {
        saleHisService.unUploadVoucher(Util1.toDate(syncDate))
                .doOnNext(vou -> accountRepo.sendSaleAsync(vou)
                        .then()
                        .subscribe())
                .delayElements(Duration.ofMillis(500))
                .subscribe();
    }

    private void uploadPurchaseVoucher() {
        purHisService.unUploadVoucher(Util1.toDate(syncDate))
                .doOnNext(vou -> accountRepo.sendPurchaseAsync(vou)
                        .then()
                        .subscribe())
                .delayElements(Duration.ofMillis(500))
                //.doOnComplete(() -> log.info("uploadPurchaseVoucher: done"))
                .subscribe();
    }


    private void uploadReturnInVoucher() {
        retInService.unUploadVoucher(Util1.toDate(syncDate))
                .doOnNext(vou -> accountRepo.sendReturnInSync(vou)
                        .then()
                        .subscribe())
                .delayElements(Duration.ofMillis(500))
                //.doOnComplete(() -> log.info("uploadReturnInVoucher: done"))
                .subscribe();

    }

    private void uploadReturnOutVoucher() {
        retOutService.unUploadVoucher(Util1.toDate(syncDate))
                .doOnNext(vou -> accountRepo.sendReturnOutSync(vou)
                        .then()
                        .subscribe())
                .delayElements(Duration.ofMillis(500))
                //.doOnComplete(() -> log.info("uploadReturnOutVoucher: done"))
                .subscribe();

    }

    private void uploadPayment() {
        paymentHisService.unUploadVoucher(Util1.toDate(syncDate))
                .doOnNext(vou -> accountRepo.sendPayment(vou)
                        .then()
                        .subscribe())
                .delayElements(Duration.ofMillis(500))
                //.doOnComplete(() -> log.info("uploadReturnOutVoucher: done"))
                .subscribe();

    }

    private void uploadLabourPayment() {
        labourPaymentService.unUploadVoucher(Util1.toDate(syncDate))
                .doOnNext(vou -> accountRepo.sendLabourPayment(vou)
                        .then()
                        .subscribe())
                .delayElements(Duration.ofMillis(500))
                //.doOnComplete(() -> log.info("uploadReturnOutVoucher: done"))
                .subscribe();

    }

}
