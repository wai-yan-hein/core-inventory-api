package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.common.Util1;
import cv.api.dao.PaymentHisDetailDao;
import cv.api.entity.PaymentHis;
import cv.api.entity.PaymentHisKey;
import cv.api.repo.AccountRepo;
import cv.api.service.PaymentHisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/payment")
public class PaymentController {
    private final PaymentHisService paymentHisService;
    private final PaymentHisDetailDao paymentHisDetailDao;
    private final AccountRepo accountRepo;

    @GetMapping(path = "/getTraderBalance")
    public Flux<?> getCustomerBalance(@RequestParam String traderCode,
                                      @RequestParam String tranOption,
                                      @RequestParam String compCode) {
        return paymentHisService.getTraderBalance(traderCode,tranOption,compCode);
    }

    @PostMapping(path = "/deletePayment")
    public Mono<?> deletePayment(@RequestBody PaymentHisKey key) {
        paymentHisService.delete(key);
        accountRepo.deleteInvVoucher(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/restorePayment")
    public Mono<?> restorePayment(@RequestBody PaymentHisKey key) {
        paymentHisService.restore(key);
        return Mono.just(true);

    }


    @PostMapping(path = "/savePayment")
    public Mono<PaymentHis> savePayment(@RequestBody PaymentHis ph) {
        ph = paymentHisService.save(ph);
        accountRepo.sendPayment(ph);
        return Mono.justOrEmpty(ph);
    }

    @PostMapping(path = "/paymentReport")
    public Flux<?> paymentReport(@RequestBody PaymentHisKey ph) {
        return Flux.fromIterable(paymentHisService.getPaymentVoucher(ph.getVouNo(), ph.getCompCode())).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/checkPaymentExist")
    public Mono<?> checkPaymentExist(@RequestBody FilterObject ph) {
        String vouNo = ph.getVouNo();
        String tranOption = ph.getTranOption();
        String compCode = ph.getCompCode();
        String traderCode = ph.getTraderCode();
        return Mono.just(paymentHisService.checkPaymentExists(vouNo, traderCode, compCode, tranOption));
    }

    @GetMapping(path = "/getPaymentDetail")
    public Flux<?> getPaymentDetail(@RequestParam String vouNo, @RequestParam String compCode) {
        return Flux.fromIterable(paymentHisDetailDao.search(vouNo, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/getPaymentHistory")
    public Flux<?> getPaymentHistory(@RequestBody FilterObject filter) {
        return paymentHisService.search(filter);
    }
}
