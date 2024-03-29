/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.PurHis;
import cv.api.entity.PurHisDetail;
import cv.api.entity.PurHisKey;
import cv.api.model.VDescription;
import cv.api.model.VPurchase;
import cv.api.repo.AccountRepo;
import cv.api.service.PurHisDetailService;
import cv.api.service.PurHisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/pur")
@Slf4j
@RequiredArgsConstructor
public class PurchaseController {

    private final PurHisService phService;
    private final PurHisDetailService pdService;
    private final AccountRepo accountRepo;

    @PostMapping(path = "/savePurchase")
    public Mono<PurHis> savePurchase(@RequestBody PurHis pur) {
        return phService.save(pur).flatMap(obj -> accountRepo.sendPurchaseAsync(obj).thenReturn(obj));

    }

    @PostMapping(path = "/getPur")
    public Flux<VPurchase> getPur(@RequestBody ReportFilter filter) {
        return phService.getPurchaseHistory(filter);
    }

    @PostMapping(path = "/deletePur")
    public Mono<Boolean> deletePur(@RequestBody PurHisKey key) {
        return phService.delete(key).map(aBoolean -> {
            accountRepo.deleteInvVoucher(key);
            return aBoolean;
        });
    }

    @PostMapping(path = "/restorePur")
    public Mono<Boolean> restorePur(@RequestBody PurHisKey key) {
        return phService.restore(key);
    }

    @PostMapping(path = "/findPur")
    public Mono<PurHis> findPur(@RequestBody PurHisKey key) {
        return phService.findById(key);
    }

    @GetMapping(path = "/getPurDetail")
    public Flux<PurHisDetail> getPurDetail(@RequestParam String vouNo,
                                           @RequestParam String compCode) {
        return pdService.search(vouNo, compCode);
    }

    @GetMapping(path = "/getDescription")
    public Flux<VDescription> getDescription(@RequestParam String str, @RequestParam String compCode, @RequestParam String tranType) {
        return phService.getDescription(Util1.cleanStr(str), compCode, tranType);
    }
}
