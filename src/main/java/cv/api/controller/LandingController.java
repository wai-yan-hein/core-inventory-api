package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.LandingHis;
import cv.api.entity.LandingHisKey;
import cv.api.service.LandingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/landing")
@Slf4j
@RequiredArgsConstructor
public class LandingController {
    private final LandingService landingService;

    @PostMapping(path = "/saveLanding")
    public Mono<?> saveLanding(@RequestBody LandingHis l) {
        return Mono.justOrEmpty(landingService.save(l));
    }

    @PostMapping(path = "/findLanding")
    public Mono<?> findLanding(@RequestBody LandingHisKey key) {
        return Mono.justOrEmpty(landingService.findByCode(key));
    }

    @PostMapping(path = "/deleteLanding")
    public Mono<?> deleteLanding(@RequestBody LandingHisKey key) {
        return Mono.justOrEmpty(landingService.delete(key));
    }

    @PostMapping(path = "/restoreLanding")
    public Mono<?> restoreLanding(@RequestBody LandingHisKey key) {
        return Mono.justOrEmpty(landingService.restore(key));
    }


    @GetMapping(path = "/getLandingHisPrice")
    public Flux<?> getLandingHisPrice(@RequestParam String vouNo, @RequestParam String compCode) {
        return Flux.fromIterable(landingService.getLandingPrice(vouNo, compCode)).onErrorResume(throwable -> Flux.empty());
    }
    @GetMapping(path = "/getLandingHisQty")
    public Flux<?> getLandingQty(@RequestParam String vouNo, @RequestParam String compCode) {
        return Flux.fromIterable(landingService.getLandingQty(vouNo, compCode)).onErrorResume(throwable -> Flux.empty());
    }
    @GetMapping(path = "/getLandingChooseGrade")
    public Mono<?> getLandingChooseQty(@RequestParam String vouNo, @RequestParam String compCode) {
        return Mono.justOrEmpty(landingService.getLandingChooseGrade(vouNo, compCode)).onErrorResume(throwable -> Mono.empty());
    }
    @GetMapping(path = "/getLandingHisGrade")
    public Flux<?> getLandingGrade(@RequestParam String vouNo, @RequestParam String compCode) {
        return Flux.fromIterable(landingService.getLandingGrade(vouNo, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/history")
    public Flux<?> getHistory(@RequestBody ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String compCode = filter.getCompCode();
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        Integer deptId = filter.getDeptId();
        boolean deleted = filter.isDeleted();
        List<LandingHis> list = landingService.getLandingHistory(fromDate, toDate, traderCode, vouNo, remark, userCode, stockCode, locCode, compCode, deptId, deleted);
        return Flux.fromIterable(list).onErrorResume(throwable -> Flux.empty());
    }
}
