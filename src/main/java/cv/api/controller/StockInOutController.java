/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.dto.StockInOutDetailDto;
import cv.api.entity.StockIOKey;
import cv.api.entity.StockInOut;
import cv.api.model.VStockIO;
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

    @PostMapping(path = "/saveStockIO")
    public Mono<StockInOut> saveStockIO(@RequestBody StockInOut dto) {
        return ioService.saveStockIO(dto);
    }

    @PostMapping(path = "/getStockIO")
    public Flux<VStockIO> getStockIO(@RequestBody ReportFilter filter) {
        return ioService.getStockIOHistory(filter);
    }

    @PostMapping(path = "/deleteStockIO")
    public Mono<Boolean> deleteStockIO(@RequestBody StockIOKey key) {
        return ioService.delete(key);
    }

    @PostMapping(path = "/restoreStockIO")
    public Mono<Boolean> restoreStockIO(@RequestBody StockIOKey key) {
        return ioService.restore(key);
    }

    @PostMapping(path = "/findStockIO")
    public Mono<StockInOut> findStockIO(@RequestBody StockIOKey key) {
        return ioService.findById(key);
    }

    @GetMapping(path = "/getStockIODetail")
    public Flux<StockInOutDetailDto> getStockIODetail(@RequestParam String vouNo, @RequestParam String compCode) {
        return ioService.search(vouNo, compCode);
    }

    @GetMapping(path = "/getStockIODetailByJob")
    public Flux<StockInOutDetailDto> getStockIODetailByJob(@RequestParam String jobId, @RequestParam String compCode) {
        return ioService.searchByJob(jobId, compCode);
    }
}
