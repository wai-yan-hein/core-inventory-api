package cv.api.controller;

import cv.api.common.ReportFilter;
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
            String tranSource = vouNo.startsWith("C") ? "RECEIVE" : "PAYMENT";
            accountRepo.deletePayment(vouNo, compCode, tranSource);
            return Mono.just(true);
        });
    }

    @DeleteMapping(path = "/restorePayment")
    public Mono<Boolean> restorePayment(@RequestParam String vouNo, @RequestParam String compCode) {
        return paymentHisService.restore(vouNo, compCode);

    }


    @PostMapping(path = "/savePayment")
    public Mono<PaymentHis> savePayment(@RequestBody PaymentHis ph) {
        return paymentHisService.save(ph).flatMap(obj -> accountRepo.sendPayment(obj).thenReturn(obj));
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
    public Flux<?> getPaymentHistory(@RequestBody ReportFilter filter) {
        return paymentHisService.search(filter);
    }
}
