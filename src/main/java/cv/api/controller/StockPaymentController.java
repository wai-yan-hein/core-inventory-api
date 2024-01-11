package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.dto.StockPayment;
import cv.api.dto.StockPaymentDetail;
import cv.api.service.StockPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/stockPayment")
public class StockPaymentController {
    private final StockPaymentService paymentService;

    @GetMapping(path = "/getTraderStockBalanceQty")
    public Flux<StockPaymentDetail> getTraderStockBalanceQty(@RequestParam String traderCode,
                                                             @RequestParam String tranOption,
                                                             @RequestParam String compCode) {
        return paymentService.calculatePaymentQty(traderCode, compCode, tranOption);
    }

    @GetMapping(path = "/getTraderStockBalanceBag")
    public Flux<StockPaymentDetail> getTraderStockBalanceBag(@RequestParam String traderCode,
                                                             @RequestParam String tranOption,
                                                             @RequestParam String compCode) {
        return paymentService.calculatePaymentBag(traderCode, compCode, tranOption);
    }

    @DeleteMapping("delete/{vouNo}/{compCode}")
    public Mono<Boolean> deletePayment(@PathVariable String vouNo, @PathVariable String compCode) {
        return paymentService.update(vouNo, compCode, true);
    }

    @PutMapping("restore/{vouNo}/{compCode}")
    public Mono<Boolean> restorePayment(@PathVariable String vouNo, @PathVariable String compCode) {
        return paymentService.update(vouNo, compCode, false);

    }

    @PostMapping(path = "/savePayment")
    public Mono<StockPayment> savePayment(@RequestBody StockPayment ph) {
        return paymentService.save(ph);
    }

    @GetMapping(path = "/getPaymentDetail")
    public Flux<StockPaymentDetail> getPaymentDetail(@RequestParam String vouNo,
                                                     @RequestParam String compCode) {
        return paymentService.getDetail(vouNo, compCode);
    }

    @PostMapping(path = "/getPaymentHistory")
    public Flux<StockPayment> getPaymentHistory(@RequestBody FilterObject filter) {
        return paymentService.history(filter);
    }
}
