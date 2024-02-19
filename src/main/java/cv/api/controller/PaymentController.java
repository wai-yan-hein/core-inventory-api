package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.entity.PaymentHis;
import cv.api.entity.PaymentHisDetail;
import cv.api.model.VSale;
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
    private final AccountRepo accountRepo;

    @GetMapping(path = "/getTraderBalance")
    public Flux<PaymentHisDetail> getCustomerBalance(@RequestParam String traderCode,
                                                     @RequestParam String tranOption,
                                                     @RequestParam String compCode) {
        return paymentHisService.getTraderBalance(traderCode, tranOption, compCode);
    }

    @GetMapping(path = "/getTraderBalanceSummary")
    public Mono<PaymentHis> getTraderBalanceSummary(@RequestParam String traderCode,
                                                    @RequestParam String tranOption,
                                                    @RequestParam String compCode) {
        return paymentHisService.getTraderBalanceSummary(traderCode, tranOption, compCode);
    }

    @DeleteMapping(path = "/deletePayment")
    public Mono<Boolean> deletePayment(@RequestParam String vouNo, @RequestParam String compCode) {
        return paymentHisService.delete(vouNo, compCode).flatMap(aBoolean -> {
            accountRepo.deleteInvVoucher(vouNo, compCode);
            return Mono.just(true);
        });
    }

    @DeleteMapping(path = "/restorePayment")
    public Mono<Boolean> restorePayment(@RequestParam String vouNo, @RequestParam String compCode) {
        paymentHisService.restore(vouNo, compCode);
        return Mono.just(true);

    }


    @PostMapping(path = "/savePayment")
    public Mono<PaymentHis> savePayment(@RequestBody PaymentHis ph) {
        return paymentHisService.save(ph).flatMap(his -> {
            accountRepo.sendPayment(his);
            return Mono.just(his);
        });
    }

    @GetMapping(path = "/paymentReport")
    public Flux<VSale> paymentReport(@RequestParam String vouNo, @RequestParam String compCode) {
        return paymentHisService.getPaymentVoucher(vouNo, compCode);
    }

    @GetMapping(path = "/getPaymentDetail")
    public Flux<PaymentHisDetail> getPaymentDetail(@RequestParam String vouNo, @RequestParam String compCode) {
        return paymentHisService.getPaymentDetail(vouNo, compCode);
    }

    @PostMapping(path = "/getPaymentHistory")
    public Flux<?> getPaymentHistory(@RequestBody FilterObject filter) {
        return paymentHisService.search(filter);
    }
}
