package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.entity.WeightHis;
import cv.api.entity.WeightHisDetail;
import cv.api.entity.WeightHisKey;
import cv.api.model.WeightColumn;
import cv.api.service.WeightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/weight")
@Slf4j
@RequiredArgsConstructor
public class WeightController {
    private final WeightService weightService;

    @PostMapping(path = "/saveWeight")
    public Mono<WeightHis> saveWeight(@RequestBody WeightHis obj) {
        return weightService.save(obj);
    }
    @PostMapping(path = "/findWeight")
    public Mono<?> saveWeight(@RequestBody WeightHisKey key) {
        return Mono.justOrEmpty(weightService.findById(key));
    }

    @PostMapping(path = "/deleteWeight")
    public Mono<Boolean> deleteWeight(@RequestBody WeightHisKey key) {
        return weightService.delete(key);
    }

    @PostMapping(path = "/restoreWeight")
    public Mono<Boolean> restoreWeight(@RequestBody WeightHisKey key) {
        return weightService.restore(key);
    }

    @GetMapping(path = "/getWeightDetail")
    public Flux<WeightHisDetail> getWeightDetail(@RequestParam String vouNo, @RequestParam String compCode) {
        return weightService.getWeightDetail(vouNo, compCode);
    }

    @GetMapping(path = "/getWeightColumn")
    public Flux<WeightColumn> getWeightColumn(@RequestParam String vouNo, @RequestParam String compCode) {
        return weightService.getWeightColumn(vouNo, compCode);
    }

    @PostMapping(path = "/getWeightHistory")
    public Flux<WeightHis> getWeightHistory(@RequestBody ReportFilter filter) {
        return weightService.getWeightHistory(filter);
    }

}
