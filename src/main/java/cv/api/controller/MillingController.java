/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.entity.*;
import cv.api.service.MillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/milling")
@Slf4j
@RequiredArgsConstructor
public class MillingController {

    private final MillingService millingService;

    @PostMapping(path = "/saveMilling")
    public Mono<MillingHis> saveMilling(@RequestBody MillingHis dto) {
        return millingService.save(dto);
    }

    @PostMapping(path = "/getMilling")
    public Flux<MillingHis> getMilling(@RequestBody ReportFilter filter) {
        return millingService.getMillingHistory(filter);
    }

    @PostMapping(path = "/deleteMilling")
    public Mono<Boolean> deleteMilling(@RequestBody MillingHisKey key) {
        return millingService.delete(key);
    }

    @PostMapping(path = "/restoreMilling")
    public Mono<Boolean> restoreMilling(@RequestBody MillingHisKey key) {
        return millingService.restore(key);
    }

    @PostMapping(path = "/findMilling")
    public Mono<MillingHis> findMilling(@RequestBody MillingHisKey key) {
        return millingService.findById(key);
    }

    @GetMapping(path = "/getRawDetail")
    public Flux<MillingRawDetail> getRawDetail(@RequestParam String vouNo,
                                               @RequestParam String compCode) {
        return millingService.getRawDetail(vouNo, compCode);
    }

    @GetMapping(path = "/getMillingExpense")
    public Flux<MillingExpense> getMillingExpense(@RequestParam String vouNo,
                                                  @RequestParam String compCode) {
        return millingService.getMillingExpense(vouNo, compCode);
    }

    @GetMapping(path = "/getOutputDetail")
    public Flux<MillingOutDetail> getOutputDetail(@RequestParam String vouNo,
                                                  @RequestParam String compCode) {
        return millingService.getOutputDetail(vouNo, compCode);
    }

    @GetMapping(path = "/getUsageDetail")
    public Flux<MillingUsage> getUsageDetail(@RequestParam String vouNo,
                                             @RequestParam String compCode) {
        return millingService.getUsageDetail(vouNo, compCode);
    }
}
