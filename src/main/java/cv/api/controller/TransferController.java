/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.TransferHis;
import cv.api.entity.TransferHisDetail;
import cv.api.entity.TransferHisKey;
import cv.api.model.VTransfer;
import cv.api.service.ReportService;
import cv.api.service.TransferHisDetailService;
import cv.api.service.TransferHisService;
import cv.api.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/transfer")
@Slf4j
@RequiredArgsConstructor
public class TransferController {

    private final TransferHisService thService;
    private final TransferHisDetailService detailService;
    private final TransferService transferService;

    @PostMapping(path = "/saveTransfer")
    public Mono<TransferHis> saveTransfer(@RequestBody TransferHis obj) {
        obj.setUpdatedDate(Util1.getTodayLocalDate());
        //save to local
        obj = thService.save(obj);
        return Mono.justOrEmpty(obj);
    }

    @PostMapping(path = "/getTransfer")
    public Flux<VTransfer> getTransfer(@RequestBody ReportFilter filter) {
        return transferService.getTransferHistory(filter);
    }

    @PostMapping(path = "/findTransfer")
    public Mono<TransferHis> findTransfer(@RequestBody TransferHisKey code) {
        TransferHis sh = thService.findById(code);
        return Mono.justOrEmpty(sh);
    }

    @GetMapping(path = "/getTransferDetail")
    public Flux<?> getTransferDetail(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer deptId) {
        List<TransferHisDetail> listSD = detailService.search(vouNo, compCode, deptId);
        return Flux.fromIterable(listSD).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/deleteTransfer")
    public Mono<?> deleteTransfer(@RequestBody TransferHisKey key) {
        thService.delete(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/restoreTransfer")
    public Mono<?> restoreTransfer(@RequestBody TransferHisKey key) {
        thService.restore(key);
        return Mono.just(true);
    }

    @GetMapping(value = "/getTransferReport")
    public Flux<VTransfer> getTransferReport(@RequestParam String vouNo,
                                             @RequestParam String compCode) {
        return transferService.getTransferVoucher(vouNo, compCode);
    }
}
