/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.dto.StockInOutDetailDto;
import cv.api.entity.StockIOKey;
import cv.api.entity.StockInOut;
import cv.api.model.VStockIO;
import cv.api.service.StockInOutDetailService;
import cv.api.service.StockInOutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/stockio")
@Slf4j
@RequiredArgsConstructor
public class StockInOutController {

    private final StockInOutService ioService;
    private final StockInOutDetailService iodService;

    @PostMapping(path = "/saveStockIO")
    public Mono<StockInOut> saveStockIO(@RequestBody StockInOut stockio) {
        stockio.setUpdatedDate(Util1.getTodayLocalDate());
        stockio = ioService.save(stockio);
        return Mono.justOrEmpty(stockio);
    }

    @PostMapping(path = "/getStockIO")
    public Flux<VStockIO> getStockIO(@RequestBody ReportFilter filter) {
        return ioService.getStockIOHistory(filter);
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
    public Flux<StockInOutDetailDto> getStockIODetail(@RequestParam String vouNo, @RequestParam String compCode) {
        return iodService.search(vouNo, compCode);
    }

    @GetMapping(path = "/getStockIODetailByJob")
    public Flux<StockInOutDetailDto> getStockIODetailByJob(@RequestParam String jobId, @RequestParam String compCode) {
        return iodService.searchByJob(jobId, compCode);
    }
}
