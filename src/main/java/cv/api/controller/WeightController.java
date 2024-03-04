package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.WeightHis;
import cv.api.entity.WeightHisKey;
import cv.api.service.WeightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/weight")
@Slf4j
@RequiredArgsConstructor
public class WeightController {
    private final WeightService weightService;

    @PostMapping(path = "/saveWeight")
    public Mono<?> saveWeight(@RequestBody WeightHis obj) {
        obj = weightService.save(obj);
        return Mono.justOrEmpty(obj);
    }

    @PostMapping(path = "/findWeight")
    public Mono<?> saveWeight(@RequestBody WeightHisKey key) {
        return Mono.justOrEmpty(weightService.findById(key));
    }

    @PostMapping(path = "/deleteWeight")
    public Mono<?> deleteWeight(@RequestBody WeightHisKey key) {
        weightService.delete(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/restoreWeight")
    public Mono<?> restoreWeight(@RequestBody WeightHisKey key) {
        weightService.restore(key);
        return Mono.just(true);
    }

    @GetMapping(path = "/getWeightDetail")
    public Flux<?> getWeightDetail(@RequestParam String vouNo, @RequestParam String compCode) {
        return Flux.fromIterable(weightService.getWeightDetail(vouNo, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getWeightColumn")
    public Flux<?> getWeightColumn(@RequestParam String vouNo, @RequestParam String compCode) {
        return Flux.fromIterable(weightService.getWeightColumn(vouNo, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/getWeightHistory")
    public Flux<?> getWeightHistory(@RequestBody ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String compCode = filter.getCompCode();
        boolean deleted = filter.isDeleted();
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String tranSource = Util1.isNull(filter.getTranSource(), "-");
        boolean draft = filter.isDraft();
        return Flux.fromIterable(weightService.getWeightHistory(fromDate, toDate, traderCode,
                        stockCode, vouNo, remark, deleted, compCode, tranSource, draft))
                .onErrorResume(throwable -> Flux.empty());
    }

}
