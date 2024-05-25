package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.entity.GRN;
import cv.api.entity.GRNDetail;
import cv.api.entity.GRNKey;
import cv.api.service.GRNService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/grn")
@Slf4j
public class GRNController {
    private final GRNService grnService;

    @PostMapping
    public Mono<GRN> saveGRN(@RequestBody GRN dto) {
        return grnService.save(dto);
    }

    @PostMapping(path = "/history")
    public Flux<GRN> getHistory(@RequestBody ReportFilter filter) {
        return grnService.getGRNHistory(filter);
    }

    @GetMapping(path = "/getGRNDetail")
    public Flux<GRNDetail> getGRNDetail(@RequestParam String vouNo, @RequestParam String compCode) {
        return grnService.getGRNDetail(vouNo, compCode);
    }

    @PostMapping(path = "/deleteGRN")
    public Mono<Boolean> deleteGRN(@RequestBody GRNKey key) {
        return grnService.delete(key);
    }

    @PostMapping(path = "/restoreGRN")
    public Mono<Boolean> restoreGRN(@RequestBody GRNKey key) {
        return grnService.restore(key);
    }

    @PostMapping(path = "/openGRN")
    public Mono<Boolean> openGRN(@RequestBody GRNKey key) {
        return grnService.open(key);
    }

    @PostMapping(path = "/findGRN")
    public Mono<GRN> findGRN(@RequestBody GRNKey key) {
        return grnService.findById(key);
    }

    @GetMapping(path = "/getBatchList")
    public Flux<GRN> getBatchList(@RequestParam String batchNo, @RequestParam String compCode) {
        return grnService.search(batchNo, compCode);
    }

}
