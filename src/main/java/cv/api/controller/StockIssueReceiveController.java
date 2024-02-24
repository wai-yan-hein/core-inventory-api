package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.ConsignHis;
import cv.api.entity.ConsignHisKey;
import cv.api.service.ReportService;
import cv.api.service.ConsignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/stockIssRec")
@Slf4j
@RequiredArgsConstructor
public class StockIssueReceiveController {
    private final ConsignService stockIssRecService;

    @Autowired
    private ReportService reportService;
    @PostMapping(path = "/saveStockIssRec")
    public Mono<?> saveStockIssRec(@RequestBody ConsignHis obj) {
        obj = stockIssRecService.save(obj);
        return Mono.justOrEmpty(obj);
    }

    @PostMapping(path = "/findStockIssRec")
    public Mono<?> findStockIssRec(@RequestBody ConsignHisKey key) {
        return Mono.justOrEmpty(stockIssRecService.findById(key));
    }

    @PostMapping(path = "/deleteStockIssRec")
    public Mono<?> deleteStockIssRec(@RequestBody ConsignHisKey key) {
        stockIssRecService.delete(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/restoreStockIssRec")
    public Mono<?> restoreStockIssRec(@RequestBody ConsignHisKey key) {
        stockIssRecService.restore(key);
        return Mono.just(true);
    }

    @GetMapping(path = "/getStockIssRecDetail")
    public Flux<?> getStockIssRecDetail(@RequestParam String vouNo, @RequestParam String compCode) {
        return Flux.fromIterable(stockIssRecService.getStockIssRecDetail(vouNo, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/getStockIssRecHistory")
    public Flux<?> getStockIssRecHistory(@RequestBody ReportFilter filter) {
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
        int tranSource = Integer.parseInt(Util1.isNull(filter.getTranSource(), "-"));
        return Flux.fromIterable(reportService.getStockIssueReceiveHistory(fromDate, toDate, traderCode,
                        userCode,stockCode, vouNo, remark,locCode, deptId,deleted, compCode, tranSource))
                .onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/findStockIR")
    public Mono<ConsignHis> findStockIR(@RequestBody ConsignHisKey key) {
        ConsignHis sh = stockIssRecService.findById(key);
        return Mono.justOrEmpty(sh);
    }

}
