package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.common.Util1;
import cv.api.dao.PaymentHisDetailDao;
import cv.api.entity.PaymentHis;
import cv.api.service.PaymentHisService;
import cv.api.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/payment")
public class PaymentController {
    @Autowired
    private ReportService reportService;
    @Autowired
    private PaymentHisService paymentHisService;
    @Autowired
    private PaymentHisDetailDao paymentHisDetailDao;

    @GetMapping(path = "/getCustomerBalance")
    public Flux<?> getCustomerBalance(@RequestParam String traderCode, @RequestParam String compCode) {
        return Flux.fromIterable(reportService.getCustomerBalance(traderCode, compCode));
    }

    @PostMapping(path = "/savePayment")
    public Mono<?> savePayment(@RequestBody PaymentHis ph) {
        return Mono.justOrEmpty(paymentHisService.save(ph));
    }
    @GetMapping(path = "/getPaymentDetail")
    public Flux<?> getPaymentDetail(@RequestParam String vouNo,@RequestParam String compCode,@RequestParam Integer deptId) {
        return Flux.fromIterable(paymentHisDetailDao.search(vouNo,compCode,deptId));
    }

    @PostMapping(path = "/getPaymentHistory")
    public Flux<?> getPaymentHistory(@RequestBody FilterObject filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String account = Util1.isAll(filter.getAccount());
        String cusCode = Util1.isNull(filter.getCusCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String compCode = filter.getCompCode();
        boolean deleted = filter.isDeleted();
        String projectNo = Util1.isAll(filter.getProjectNo());
        String curCode = Util1.isAll(filter.getCurCode());
        return Flux.fromIterable(paymentHisService.search(fromDate, toDate, cusCode, curCode,vouNo,userCode,account, projectNo, remark, deleted, compCode));
    }
}
