/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.entity.OrderHis;
import cv.api.entity.OrderHisDetail;
import cv.api.entity.OrderHisKey;
import cv.api.model.VDescription;
import cv.api.service.OrderHisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author wai yan
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    private final OrderHisService ohService;

    @PostMapping(path = "/saveOrder")
    public Mono<OrderHis> saveOrder(@RequestBody OrderHis dto) {
        return ohService.saveOrder(dto);
    }

    @PostMapping(path = "/getOrder")
    public Flux<OrderHis> getOrder(@RequestBody ReportFilter filter) {
        return ohService.getOrderHistory(filter);
    }

    @PostMapping(path = "/deleteOrder")
    public Mono<Boolean> deleteOrder(@RequestBody OrderHisKey key) {
        return ohService.delete(key);
    }

    @PostMapping(path = "/restoreOrder")
    public Mono<Boolean> restoreOrder(@RequestBody OrderHisKey key) {
        return ohService.restore(key);
    }

    @PostMapping(path = "/findOrder")
    public Mono<OrderHis> findOrder(@RequestBody OrderHisKey key) {
        return ohService.findById(key);
    }

    @GetMapping(path = "/getOrderDetail")
    public Flux<OrderHisDetail> getOrderDetail(@RequestParam String vouNo,
                                               @RequestParam String compCode) {
        return ohService.searchDetail(vouNo, compCode);
    }

    @GetMapping(value = "/getOrderReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<OrderHisDetail> getOrderReport(@RequestParam String vouNo,
                                               @RequestParam String compCode,
                                               @RequestParam Integer macId) {
        return ohService.getOrderVoucher(vouNo, compCode);
    }

    @PostMapping(path = "/getOrderSummaryByDepartment")
    public Flux<OrderHis> getOrderSummaryByDepartment(@RequestBody ReportFilter filter) {
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String compCode = filter.getCompCode();
        return ohService.getOrderSummaryByDepartment(fromDate, toDate, compCode);
    }

    @GetMapping(path = "/getDesign")
    public Flux<VDescription> getDesign(@RequestParam String str,
                                        @RequestParam String compCode) {
        return ohService.getDesign(str, compCode);
    }

    @GetMapping(path = "/getSize")
    public Flux<VDescription> getSize(@RequestParam String str,
                                      @RequestParam String compCode) {
        return ohService.getSize(str, compCode);
    }
    @GetMapping(path = "/searchByRefNo")
    public Flux<OrderHis> searchByRefNo(@RequestParam String refNo,
                                      @RequestParam String compCode) {
        return ohService.searchByRefNo(refNo, compCode);
    }
}
