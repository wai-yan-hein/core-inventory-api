package cv.api.auto;

import cv.api.common.Util1;
import cv.api.entity.*;
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
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@PropertySource("file:config/application.properties")
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
                .subscribe();
    }


    private void uploadSaleVoucher() {
        List<SaleHis> vouchers = saleHisService.unUploadVoucher(Util1.toDate(syncDate));
        if (!vouchers.isEmpty()) {
            log.info(String.format("uploadSaleVoucher: %s", vouchers.size()));
            vouchers.forEach(vou -> {
                if (vou.isDeleted()) {
                    accountRepo.deleteInvVoucher(vou.getKey());
                } else {
                    accountRepo.sendSale(vou);
                    sleep();
                }
            });
            log.info(String.format("uploadSaleVoucher: %s", "done"));
        }
    }

    private void sleep() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private void uploadPurchaseVoucher() {
        List<PurHis> vouchers = purHisService.unUploadVoucher(Util1.toDate(syncDate));
        if (!vouchers.isEmpty()) {
            log.info(String.format("uploadPurchaseVoucher: %s", vouchers.size()));
            vouchers.forEach(vou -> {
                if (vou.isDeleted()) {
                    accountRepo.deleteInvVoucher(vou.getKey());
                } else {
                    accountRepo.sendPurchase(vou);
                }
            });
        }
    }


    private void uploadReturnInVoucher() {
        List<RetInHis> vouchers = retInService.unUploadVoucher(Util1.toDate(syncDate));
        if (!vouchers.isEmpty()) {
            log.info(String.format("uploadReturnInVoucher: %s", vouchers.size()));
            vouchers.forEach(vou -> {
                if (vou.isDeleted()) {
                    accountRepo.deleteInvVoucher(vou.getKey());
                } else {
                    accountRepo.sendReturnIn(vou);
                }
            });
        }
    }

    private void uploadReturnOutVoucher() {
        List<RetOutHis> vouchers = retOutService.unUploadVoucher(Util1.toDate(syncDate));
        if (!vouchers.isEmpty()) {
            log.info(String.format("uploadReturnOutVoucher: %s", vouchers.size()));
            vouchers.forEach(vou -> {
                if (vou.isDeleted()) {
                    accountRepo.deleteInvVoucher(vou.getKey());
                } else {
                    accountRepo.sendReturnOut(vou);
                }
            });
        }
    }

    private void uploadPayment() {
        List<PaymentHis> vouchers = paymentHisService.unUploadVoucher(Util1.toDate(syncDate));
        if (!vouchers.isEmpty()) {
            log.info("uploadPayment : " + vouchers.size());
            vouchers.forEach(vou -> {
                if (vou.isDeleted()) {
                    accountRepo.deleteInvVoucher(vou.getKey());
                } else {
                    accountRepo.sendPayment(vou);
                }
            });
        }
    }

    private void uploadLabourPayment() {
        labourPaymentService.unUploadVoucher(Util1.toDate(syncDate))
                .collectList()
                .doOnNext(vouList -> {
                    if (!vouList.isEmpty()) {
                        log.info("uploadLabourPayment : " + vouList.size());
                        vouList.forEach(vou -> {
                            if (vou.isDeleted()) {
                                accountRepo.deleteVoucher(vou.getVouNo(), vou.getCompCode(), "LABOUR_PAYMENT");
                            } else {
                                accountRepo.sendLabourPayment(vou.buildDto());
                            }
                        });
                    }
                }).subscribe();
    }

}
