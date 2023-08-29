/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.common.Util1;
import cv.api.entity.StockIOKey;
import cv.api.entity.StockInOut;
import cv.api.entity.StockInOutDetail;
import cv.api.model.VStockIO;
import cv.api.service.ReportService;
import cv.api.service.StockInOutDetailService;
import cv.api.service.StockInOutService;
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
@RequestMapping("/stockio")
@Slf4j
public class StockInOutController {

    @Autowired
    private StockInOutService ioService;
    @Autowired
    private StockInOutDetailService iodService;
    @Autowired
    private ReportService reportService;

    @PostMapping(path = "/saveStockIO")
    public Mono<StockInOut> saveStockIO(@RequestBody StockInOut stockio) {
        stockio.setUpdatedDate(Util1.getTodayLocalDate());
        stockio = ioService.save(stockio);
        return Mono.justOrEmpty(stockio);
    }

    @PostMapping(path = "/g")
    public Flux<?> getStockIO(@RequestBody FilterObject filter) throws Exception {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String description = Util1.isNull(filter.getDescription(), "-");
        String vouStatus = Util1.isNull(filter.getVouStatus(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String compCode = filter.getCompCode();
        Integer deptId = filter.getDeptId();
        String deleted = String.valueOf(filter.isDeleted());
        List<VStockIO> listStockIO = reportService.getStockIOHistory(fromDate, toDate, vouStatus, vouNo, remark, description, userCode, stockCode, locCode, compCode, deptId, deleted);
        return Flux.fromIterable(listStockIO).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/deleteStockIO")
    public Mono<?> deleteStockIO(@RequestBody StockIOKey key) throws Exception {
        ioService.delete(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/restoreStockIO")
    public Mono<?> restoreStockIO(@RequestBody StockIOKey key) throws Exception {
        ioService.restore(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/findStockIO")
    public Mono<StockInOut> findStockIO(@RequestBody StockIOKey key) {
        StockInOut sh = ioService.findById(key);
        return Mono.justOrEmpty(sh);
    }

    @GetMapping(path = "/getStockIODetail")
    public Flux<?> getStockIODetail(@RequestParam String vouNo, @RequestParam String compCode) {
        List<StockInOutDetail> listSD = iodService.search(vouNo, compCode);
        return Flux.fromIterable(listSD).onErrorResume(throwable -> Flux.empty());
    }
}
