/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.entity.RetOutHis;
import cv.api.entity.RetOutHisDetail;
import cv.api.entity.RetOutHisKey;
import cv.api.repo.AccountRepo;
import cv.api.service.RetOutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/retOut")
@Slf4j
@RequiredArgsConstructor
public class RetOutController {

    private final RetOutService roService;
    private final AccountRepo accountRepo;

    @PostMapping(path = "/saveReturnOut")
    public Mono<RetOutHis> saveReturnOut(@RequestBody RetOutHis ro) {
        return roService.save(ro).flatMap(obj -> accountRepo.sendReturnOutSync(obj).thenReturn(obj));

    }

    @PostMapping(path = "/getReturnOut")
    public Flux<RetOutHis> getReturnOut(@RequestBody ReportFilter filter) {
        return roService.getHistory(filter);
    }

    @PostMapping(path = "/deleteReturnOut")
    public Mono<Boolean> deleteReturnOut(@RequestBody RetOutHisKey key) {
        return roService.delete(key).flatMap(aBoolean -> {
            accountRepo.deleteInvVoucher(key);
            return Mono.just(aBoolean);
        });
    }

    @PostMapping(path = "/restoreReturnOut")
    public Mono<Boolean> restoreReturnOut(@RequestBody RetOutHisKey key) {
        return roService.restore(key);
    }

    @PostMapping(path = "/findReturnOut")
    public Mono<RetOutHis> findReturnOut(@RequestBody RetOutHisKey key) {
        return roService.findById(key);
    }

    @GetMapping(path = "/getReturnOutDetail")
    public Flux<RetOutHisDetail> getReturnOutDetail(@RequestParam String vouNo, @RequestParam String compCode) {
        return roService.getRetOutDetail(vouNo, compCode);
    }

    @GetMapping(value = "/getReturnOutReport")
    public Flux<RetOutHisDetail> getReturnOutReport(@RequestParam String vouNo,
                                                    @RequestParam String compCode) {
        return roService.getReturnOutVoucher(vouNo, compCode);
    }

}
