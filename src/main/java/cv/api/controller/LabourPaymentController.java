package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.common.ReportFilter;
import cv.api.dto.LabourPaymentDto;
import cv.api.r2dbc.LabourPayment;
import cv.api.r2dbc.LabourPaymentDetail;
import cv.api.repo.AccountRepo;
import cv.api.service.LabourPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/labourPayment")
public class LabourPaymentController {
    private final LabourPaymentService labourPaymentService;
    private final AccountRepo accountRepo;

    @PostMapping
    public Mono<LabourPayment> savePayment(@RequestBody LabourPaymentDto dto) {
        return labourPaymentService.save(dto).flatMap(payment -> {
            accountRepo.sendLabourPayment(payment);
            return Mono.just(payment);
        });
    }

    @PostMapping("/calculatePayment")
    public Flux<LabourPaymentDetail> calculatePayment(@RequestBody ReportFilter filter) {
        return labourPaymentService.calculatePayment(filter);
    }

    @PostMapping("/history")
    public Flux<LabourPaymentDto> history(@RequestBody FilterObject filter) {
        return labourPaymentService.history(filter).delayElements(Duration.ofSeconds(3
        ));
    }

    @GetMapping("/getDetail")
    public Flux<LabourPaymentDetail> getDetail(@RequestParam String vouNo, @RequestParam String compCode) {
        return labourPaymentService.getDetail(vouNo, compCode);
    }
}
