package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.dto.LabourPaymentDto;
import cv.api.entity.LabourPaymentDetail;
import cv.api.repo.AccountRepo;
import cv.api.service.LabourPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/labourPayment")
public class LabourPaymentController {
    private final LabourPaymentService labourPaymentService;
    private final AccountRepo accountRepo;

    @PostMapping
    public Mono<LabourPaymentDto> savePayment(@RequestBody LabourPaymentDto dto) {
        return labourPaymentService.save(dto).flatMap(obj -> accountRepo.sendLabourPayment(obj).thenReturn(obj));

    }

    @PostMapping("/calculatePayment")
    public Flux<LabourPaymentDetail> calculatePayment(@RequestBody ReportFilter filter) {
        return labourPaymentService.calculatePayment(filter);
    }

    @PostMapping("/history")
    public Flux<LabourPaymentDto> history(@RequestBody ReportFilter filter) {
        return labourPaymentService.history(filter);
    }

    @GetMapping("/getDetail")
    public Flux<LabourPaymentDetail> getDetail(@RequestParam String vouNo, @RequestParam String compCode) {
        return labourPaymentService.getDetail(vouNo, compCode);
    }

    @DeleteMapping("delete/{vouNo}/{compCode}")
    public Mono<Boolean> delete(@PathVariable String vouNo, @PathVariable String compCode) {
        return labourPaymentService.update(vouNo, compCode, true).flatMap(delete -> {
            accountRepo.deleteVoucher(vouNo, compCode, "LABOUR_PAYMENT");
            return Mono.just(true);
        });
    }

    @PutMapping("restore/{vouNo}/{compCode}")
    public Mono<Boolean> restore(@PathVariable String vouNo, @PathVariable String compCode) {
        return labourPaymentService.update(vouNo, compCode, false);
    }
}
