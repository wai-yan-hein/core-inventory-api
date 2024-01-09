package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.common.Util1;
import cv.api.dao.PaymentHisDetailDao;
import cv.api.entity.PaymentHis;
import cv.api.entity.PaymentHisKey;
import cv.api.repo.AccountRepo;
import cv.api.service.PaymentHisService;
import cv.api.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/stockPayment")
public class StockPaymentController {
    private final ReportService reportService;
    private final PaymentHisService paymentHisService;
    private final PaymentHisDetailDao paymentHisDetailDao;
    private final AccountRepo accountRepo;

    @GetMapping(path = "/getTraderBalance")
    public Flux<?> getCustomerBalance(@RequestParam String traderCode,
                                      @RequestParam String tranOption,
                                      @RequestParam String compCode) {
        return Flux.fromIterable(reportService.getTraderBalance(traderCode, tranOption, compCode)).onErrorResume(throwable -> Flux.empty());
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
    public Mono<?> savePayment(@RequestBody PaymentHis ph) {
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
    public Flux<?> getPaymentDetail(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer deptId) {
        return Flux.fromIterable(paymentHisDetailDao.search(vouNo, compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/getPaymentHistory")
    public Flux<?> getPaymentHistory(@RequestBody FilterObject filter) {
        String tranOption = Util1.isNull(filter.getTranOption(), "-");
        if (tranOption.equals("C") || tranOption.equals("S")) {
            String fromDate = Util1.isNull(filter.getFromDate(), "-");
            String toDate = Util1.isNull(filter.getToDate(), "-");
            String vouNo = Util1.isNull(filter.getVouNo(), "-");
            String saleVouNo = Util1.isNull(filter.getSaleVouNo(), "-");
            String userCode = Util1.isNull(filter.getUserCode(), "-");
            String account = Util1.isAll(filter.getAccount());
            String traderCode = Util1.isNull(filter.getTraderCode(), "-");
            String remark = Util1.isNull(filter.getRemark(), "-");
            String compCode = filter.getCompCode();
            boolean deleted = filter.isDeleted();
            String projectNo = Util1.isAll(filter.getProjectNo());
            String curCode = Util1.isAll(filter.getCurCode());
            return Flux.fromIterable(paymentHisService.search(fromDate, toDate, traderCode,
                            curCode, vouNo, saleVouNo, userCode, account, projectNo, remark,
                            deleted, compCode, tranOption))
                    .onErrorResume(throwable -> Flux.empty());
        }
        return Flux.empty();
    }
}
