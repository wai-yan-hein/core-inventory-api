package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.entity.ConsignHis;
import cv.api.entity.ConsignHisDetail;
import cv.api.entity.ConsignHisKey;
import cv.api.model.VConsign;
import cv.api.service.ConsignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/consign")
@Slf4j
@RequiredArgsConstructor
public class ConsignController {
    private final ConsignService consignService;

    @PostMapping
    public Mono<ConsignHis> saveStockIssRec(@RequestBody ConsignHis obj) {
        return consignService.save(obj);
    }

    @PostMapping(path = "/findConsign")
    public Mono<ConsignHis> findStockIssRec(@RequestBody ConsignHisKey key) {
        return consignService.findById(key);
    }

    @PostMapping(path = "/deleteConsign")
    public Mono<Boolean> deleteStockIssRec(@RequestBody ConsignHisKey key) {
        return consignService.delete(key);
    }

    @PostMapping(path = "/restoreConsign")
    public Mono<Boolean> restoreStockIssRec(@RequestBody ConsignHisKey key) {
        return consignService.restore(key);
    }

    @GetMapping(path = "/getConsignDetail")
    public Flux<ConsignHisDetail> getConsignDetail(@RequestParam String vouNo, @RequestParam String compCode) {
        return consignService.getConsignDetail(vouNo, compCode);
    }

    @PostMapping(path = "/getConsignHistory")
    public Flux<VConsign> getConsignHistory(@RequestBody ReportFilter filter) {
        return consignService.getConsignHistory(filter);
    }


}
