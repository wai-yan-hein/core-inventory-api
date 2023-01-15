package cv.api.auto;

import cv.api.common.Util1;
import cv.api.inv.entity.*;
import cv.api.inv.service.*;
import cv.api.repo.AccountRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@PropertySource("file:config/application.properties")
public class AutoUpload {
    @Value("${sync.date}")
    private String syncDate;
    @Autowired
    private SaleHisService saleHisService;
    @Autowired
    private PurHisService purHisService;
    @Autowired
    private RetInService retInService;
    @Autowired
    private RetOutService retOutService;
    @Autowired
    private TraderService traderService;
    private boolean syncing = false;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private Environment environment;

    @Scheduled(fixedRate = 1000000)
    private void autoUpload() {
        if (Util1.getBoolean(environment.getProperty("integration"))) {
            if (!syncing) {
                syncing = true;
                log.info("autoUpload: Start");
                uploadTrader();
                uploadSaleVoucher();
                uploadPurchaseVoucher();
                uploadReturnInVoucher();
                uploadReturnOutVoucher();
                log.info("autoUpload: End");
                syncing = false;
            }
        }
    }

    private void uploadTrader() {
        List<Trader> traders = traderService.unUploadTrader();
        if (!traders.isEmpty()) {
            log.info(String.format("uploadTrader: %s", traders.size()));
            traders.forEach(vou -> accountRepo.sendTrader(vou));
        }
    }

    private void uploadSaleVoucher() {
        List<SaleHis> vouchers = saleHisService.unUploadVoucher(syncDate);
        if (!vouchers.isEmpty()) {
            log.info(String.format("uploadSaleVoucher: %s", vouchers.size()));
            vouchers.forEach(vou -> accountRepo.sendSale(vou));
        }
    }

    private void uploadPurchaseVoucher() {
        List<PurHis> vouchers = purHisService.unUploadVoucher(syncDate);
        if (!vouchers.isEmpty()) {
            log.info(String.format("uploadPurchaseVoucher: %s", vouchers.size()));
            vouchers.forEach(vou -> accountRepo.sendPurchase(vou));
        }
    }

    private void uploadReturnInVoucher() {
        List<RetInHis> vouchers = retInService.unUploadVoucher(syncDate);
        if (!vouchers.isEmpty()) {
            log.info(String.format("uploadReturnInVoucher: %s", vouchers.size()));
            vouchers.forEach(vou -> accountRepo.sendReturnIn(vou));
        }
    }

    private void uploadReturnOutVoucher() {
        List<RetOutHis> vouchers = retOutService.unUploadVoucher(syncDate);
        if (!vouchers.isEmpty()) {
            log.info(String.format("uploadReturnOutVoucher: %s", vouchers.size()));
            vouchers.forEach(vou -> accountRepo.sendReturnOut(vou));
        }
    }
}
