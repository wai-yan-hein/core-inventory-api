package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.entity.*;
import cv.api.service.LandingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/landing")
@Slf4j
@RequiredArgsConstructor
public class LandingController {
    private final LandingService landingService;

    @PostMapping(path = "/saveLanding")
    public Mono<?> saveLanding(@RequestBody LandingHis l) {
        return Mono.justOrEmpty(landingService.save(l));
    }

    @PostMapping(path = "/findLanding")
    public Mono<?> findLanding(@RequestBody LandingHisKey key) {
        return Mono.justOrEmpty(landingService.findByCode(key));
    }

    @PostMapping(path = "/deleteLanding")
    public Mono<Boolean> deleteLanding(@RequestBody LandingHisKey key) {
        return landingService.delete(key);
    }

    @PostMapping(path = "/restoreLanding")
    public Mono<Boolean> restoreLanding(@RequestBody LandingHisKey key) {
        return landingService.restore(key);
    }


    @GetMapping(path = "/getLandingHisPrice")
    public Flux<LandingHisPrice> getLandingHisPrice(@RequestParam String vouNo, @RequestParam String compCode) {
        return landingService.getLandingPrice(vouNo, compCode);
    }

    @GetMapping(path = "/getLandingHisQty")
    public Flux<LandingHisQty> getLandingQty(@RequestParam String vouNo, @RequestParam String compCode) {
        return landingService.getLandingQty(vouNo, compCode);
    }

    @GetMapping(path = "/getLandingChooseGrade")
    public Mono<LandingHisGrade> getLandingChooseQty(@RequestParam String vouNo, @RequestParam String compCode) {
        return landingService.getLandingChooseGrade(vouNo, compCode);
    }

    @GetMapping(path = "/getLandingHisGrade")
    public Flux<LandingHisGrade> getLandingGrade(@RequestParam String vouNo, @RequestParam String compCode) {
        return landingService.getLandingGrade(vouNo, compCode);
    }

    @PostMapping(path = "/history")
    public Flux<LandingHis> getHistory(@RequestBody ReportFilter filter) {
        return landingService.getLandingHistory(filter);
    }
}
