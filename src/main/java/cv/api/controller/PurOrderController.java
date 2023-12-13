package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.common.Util1;
import cv.api.entity.PurOrderHis;
import cv.api.entity.PurOrderHisKey;
import cv.api.service.ReportService;
import cv.api.service.PurOrderHisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/purOrder")
@Slf4j
@RequiredArgsConstructor
public class PurOrderController {
    private final PurOrderHisService PurOrderHisService;

    @Autowired
    private ReportService reportService;
    @PostMapping(path = "/savePurOrder")
    public Mono<?> savePurOrderHis(@RequestBody PurOrderHis obj) {
        obj = PurOrderHisService.save(obj);
        return Mono.justOrEmpty(obj);
    }

    @PostMapping(path = "/findPurOrder")
    public Mono<?> findPurOrderHis(@RequestBody PurOrderHisKey key) {
        return Mono.justOrEmpty(PurOrderHisService.findById(key));
    }

    @PostMapping(path = "/deletePurOrder")
    public Mono<?> deletePurOrderHis(@RequestBody PurOrderHisKey key) {
        PurOrderHisService.delete(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/restorePurOrder")
    public Mono<?> restorePurOrderHis(@RequestBody PurOrderHisKey key) {
        PurOrderHisService.restore(key);
        return Mono.just(true);
    }

    @GetMapping(path = "/getPurOrderHisDetail")
    public Flux<?> getPurOrderHisDetail(@RequestParam String vouNo, @RequestParam String compCode) {
        return Flux.fromIterable(PurOrderHisService.getPurOrderHisDetail(vouNo, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/getPurOrderHisHistory")
    public Flux<?> getPurOrderHisHistory(@RequestBody FilterObject filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String compCode = filter.getCompCode();
        Integer deptId = filter.getDeptId();
        boolean deleted = filter.isDeleted();
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        int tranSource = Integer.valueOf(Util1.isNull(filter.getTranSource(), "-"));
        return Flux.fromIterable(reportService.getPurOrderHistory(fromDate, toDate, traderCode,
                        userCode,stockCode, vouNo, remark,locCode, deptId,deleted, compCode, tranSource))
                .onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/findPurOrderHis")
    public Mono<PurOrderHis> findStockIR(@RequestBody PurOrderHisKey key) {
        PurOrderHis sh = PurOrderHisService.findById(key);
        return Mono.justOrEmpty(sh);
    }

}
