/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.entity.TransferHis;
import cv.api.entity.TransferHisDetail;
import cv.api.entity.TransferHisKey;
import cv.api.model.VTransfer;
import cv.api.service.TransferHisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/transfer")
@Slf4j
@RequiredArgsConstructor
public class TransferController {

    private final TransferHisService thService;

    @PostMapping(path = "/saveTransfer")
    public Mono<TransferHis> saveTransfer(@RequestBody TransferHis obj) {
        return thService.saveTransfer(obj);
    }

    @PostMapping(path = "/getTransfer")
    public Flux<VTransfer> getTransfer(@RequestBody ReportFilter filter) {
        return thService.getTransferHistory(filter);
    }

    @PostMapping(path = "/findTransfer")
    public Mono<TransferHis> findTransfer(@RequestBody TransferHisKey key) {
        return thService.findById(key);
    }

    @GetMapping(path = "/getTransferDetail")
    public Flux<TransferHisDetail> getTransferDetail(@RequestParam String vouNo, @RequestParam String compCode) {
        return thService.search(vouNo, compCode);
    }

    @PostMapping(path = "/deleteTransfer")
    public Mono<Boolean> deleteTransfer(@RequestBody TransferHisKey key) {
        return thService.delete(key);
    }

    @PostMapping(path = "/restoreTransfer")
    public Mono<Boolean> restoreTransfer(@RequestBody TransferHisKey key) {
        return thService.restore(key);
    }

    @GetMapping(value = "/getTransferReport")
    public Flux<VTransfer> getTransferReport(@RequestParam String vouNo,
                                             @RequestParam String compCode) {
        return thService.getTransferVoucher(vouNo, compCode);
    }
}
