/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.common.Util1;
import cv.api.entity.TransferHis;
import cv.api.entity.TransferHisDetail;
import cv.api.entity.TransferHisKey;
import cv.api.model.VTransfer;
import cv.api.service.ReportService;
import cv.api.service.TransferHisDetailService;
import cv.api.service.TransferHisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TransferController {

    @Autowired
    private TransferHisService thService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private TransferHisDetailService detailService;

    @PostMapping(path = "/save-transfer")
    public Mono<TransferHis> saveTransfer(@RequestBody TransferHis obj) {
        obj.setUpdatedDate(Util1.getTodayLocalDate());
        //save to local
        obj = thService.save(obj);
        return Mono.justOrEmpty(obj);
    }

    @PostMapping(path = "/get-transfer")
    public Flux<?> getTransfer(@RequestBody FilterObject filter) throws Exception {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String refNo = Util1.isNull(filter.getRefNo(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String compCode = filter.getCompCode();
        Integer deptId = filter.getDeptId();
        String deleted = String.valueOf(filter.isDeleted());
        List<VTransfer> listStockIO = reportService.getTransferHistory(fromDate, toDate, refNo,
                vouNo, remark, userCode,
                stockCode, locCode, compCode, deptId, deleted);
        return Flux.fromIterable(listStockIO);
    }

    @PostMapping(path = "/find-transfer")
    public Mono<TransferHis> findTransfer(@RequestBody TransferHisKey code) {
        TransferHis sh = thService.findById(code);
        return Mono.justOrEmpty(sh);
    }

    @GetMapping(path = "/get-transfer-detail")
    public Flux<?> getPurDetail(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer deptId) {
        List<TransferHisDetail> listSD = detailService.search(vouNo, compCode, deptId);
        return Flux.fromIterable(listSD);
    }

    @PostMapping(path = "/delete-transfer")
    public Mono<?> deleteTransfer(@RequestBody TransferHisKey key) {
        thService.delete(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/restore-transfer")
    public Mono<?> restoreTransfer(@RequestBody TransferHisKey key) {
        thService.restore(key);
        return Mono.just(true);
    }
}
