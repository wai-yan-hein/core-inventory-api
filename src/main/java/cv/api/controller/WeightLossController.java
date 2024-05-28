/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.entity.WeightLossHis;
import cv.api.entity.WeightLossHisDetail;
import cv.api.entity.WeightLossHisKey;
import cv.api.service.WeightLossService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/weightLoss")
@Slf4j
@RequiredArgsConstructor
public class WeightLossController {
    private final WeightLossService weightLossService;

    @PostMapping(path = "/saveWeightLoss")
    public Mono<WeightLossHis> saveWeightLoss(@RequestBody WeightLossHis dto) {
        return weightLossService.save(dto);
    }

    @PostMapping(path = "/getWeightLoss")
    public Flux<WeightLossHis> getWeightLoss(@RequestBody ReportFilter filter) {
        return weightLossService.getWeightLossHistory(filter);
    }

    @PostMapping(path = "/deleteWeightLoss")
    public Mono<Boolean> deleteWeightLoss(@RequestBody WeightLossHisKey key) {
        return weightLossService.delete(key);
    }

    @PostMapping(path = "/restoreWeightLoss")
    public Mono<Boolean> restoreWeightLoss(@RequestBody WeightLossHisKey key) {
        return weightLossService.restore(key);
    }

    @PostMapping(path = "/findWeightLoss")
    public Mono<WeightLossHis> find(@RequestBody WeightLossHisKey key) {
        return weightLossService.findById(key);
    }

    @GetMapping(path = "/getWeightLossDetail")
    public Flux<WeightLossHisDetail> getWeightLossDetail(@RequestParam String vouNo,
                                                         @RequestParam String compCode) {
        return weightLossService.search(vouNo, compCode);
    }
}
