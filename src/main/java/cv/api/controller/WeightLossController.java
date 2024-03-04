/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.WeightLossHis;
import cv.api.entity.WeightLossHisKey;
import cv.api.service.ReportService;
import cv.api.service.WeightLossDetailService;
import cv.api.service.WeightLossService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/weight")
@Slf4j
public class WeightLossController {
    @Autowired
    private WeightLossService weightLossService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private WeightLossDetailService weightLossDetailService;

    @PostMapping(path = "/saveWeightLoss")
    public Mono<?> saveWeightLoss(@RequestBody WeightLossHis w) {
        return Mono.justOrEmpty(weightLossService.save(w));
    }

    @PostMapping(path = "/getWeightLoss")
    public Flux<?> getWeightLoss(@RequestBody ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String refNo = Util1.isNull(filter.getRefNo(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String compCode = filter.getCompCode();
        Integer deptId = filter.getDeptId();
        String deleted = String.valueOf(filter.isDeleted());
        return Flux.fromIterable(reportService.getWeightLossHistory(fromDate, toDate, refNo, vouNo, remark, stockCode,
                locCode, compCode, deptId, deleted)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/deleteWeightLoss")
    public Mono<?> deleteWeightLoss(@RequestBody WeightLossHisKey key) {
        weightLossService.delete(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/restoreWeightLoss")
    public Mono<?> restoreWeightLoss(@RequestBody WeightLossHisKey key) {
        weightLossService.restore(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/findWeightLoss")
    public Mono<?> find(@RequestBody WeightLossHisKey key) {
        return Mono.justOrEmpty(weightLossService.findById(key));
    }

    @GetMapping(path = "/getWeightLossDetail")
    public Flux<?> getWeightLossDetail(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer deptId) {
        return Flux.fromIterable(weightLossDetailService.search(vouNo, compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }
}
