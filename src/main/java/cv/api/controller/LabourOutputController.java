/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.entity.LabourOutput;
import cv.api.entity.LabourOutputDetail;
import cv.api.service.LabourOutputService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author wai yan
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/labourOutput")
@Slf4j
public class LabourOutputController {

    private final LabourOutputService outputService;

    @PostMapping
    public Mono<LabourOutput> saveLabourOutput(@RequestBody LabourOutput dto) {
        return outputService.saveLabourOutput(dto);
    }

    @PostMapping(path = "/getHistory")
    public Flux<LabourOutput> getHistory(@RequestBody ReportFilter filter) {
        return outputService.getHistory(filter);
    }

    @DeleteMapping
    public Mono<Boolean> delete(@RequestParam String vouNo, @RequestParam String compCode) {
        return outputService.delete(vouNo, compCode);
    }

    @PutMapping
    public Mono<Boolean> restore(@RequestParam String vouNo, @RequestParam String compCode) {
        return outputService.restore(vouNo, compCode);
    }

    @GetMapping(path = "/findLabourOutput")
    public Mono<LabourOutput> findLabourOutput(@RequestParam String vouNo, @RequestParam String compCode) {
        return outputService.findById(vouNo, compCode);
    }

    @GetMapping(path = "/getLabourOutputDetail")
    public Flux<LabourOutputDetail> getLabourOutputDetail(@RequestParam String vouNo,
                                                          @RequestParam String compCode) {
        return outputService.getLabourOutputDetail(vouNo, compCode);
    }
}
