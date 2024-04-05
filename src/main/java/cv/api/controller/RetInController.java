/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.entity.RetInHis;
import cv.api.entity.RetInHisDetail;
import cv.api.entity.RetInHisKey;
import cv.api.repo.AccountRepo;
import cv.api.service.RetInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/returnIn")
@Slf4j
@RequiredArgsConstructor
public class RetInController {

    private final RetInService riService;
    private final AccountRepo accountRepo;

    @PostMapping(path = "/saveReturnIn")
    public Mono<RetInHis> saveReturnIn(@RequestBody RetInHis ri) {
        return riService.save(ri).flatMap(obj -> accountRepo.sendReturnInSync(obj).thenReturn(obj));

    }

    @PostMapping(path = "/getReturnIn")
    public Flux<RetInHis> getReturnIn(@RequestBody ReportFilter filter) {
        return riService.getHistory(filter);
    }

    @PostMapping(path = "/deleteReturnIn")
    public Mono<Boolean> deleteReturnIn(@RequestBody RetInHisKey key) {
        return riService.delete(key).flatMap(aBoolean -> {
            accountRepo.deleteInvVoucher(key);
            return Mono.just(aBoolean);
        });
    }

    @PostMapping(path = "/restoreReturnIn")
    public Mono<Boolean> restoreReturnIn(@RequestBody RetInHisKey key) {
        return riService.restore(key);
    }

    @PostMapping(path = "/findReturnIn")
    public Mono<RetInHis> findReturnIn(@RequestBody RetInHisKey key) {
        return riService.findById(key);
    }

    @GetMapping(path = "/getReturnInDetail")
    public Flux<RetInHisDetail> getReturnInDetail(@RequestParam String vouNo, @RequestParam String compCode) {
        return riService.search(vouNo, compCode);
    }

    @GetMapping(value = "/getReturnInReport")
    public Flux<RetInHisDetail> getReturnInReport(@RequestParam String vouNo, @RequestParam String compCode) {
        return riService.getReturnInVoucher(vouNo, compCode);
    }
}
