package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.ProcessHis;
import cv.api.entity.ProcessHisDetail;
import cv.api.entity.ProcessHisKey;
import cv.api.service.ProcessHisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/process")
@Slf4j
@RequiredArgsConstructor
public class ProcessController {
    private final ProcessHisService processHisService;

    @PostMapping(path = "/saveProcess")
    public Mono<ProcessHis> saveProcess(@RequestBody ProcessHis p) {
        return processHisService.save(p);
    }

    @PostMapping(path = "/deleteProcess")
    public Mono<Boolean> deleteProcess(@RequestBody ProcessHisKey p) {
        return processHisService.delete(p);
    }

    @PostMapping(path = "/findProcess")
    public Mono<ProcessHis> findProcess(@RequestBody ProcessHisKey p) {
        return processHisService.findById(p);
    }

    @PostMapping(path = "/restoreProcess")
    public Mono<Boolean> restoreProcess(@RequestBody ProcessHisKey p) {
        return processHisService.restore(p);
    }

    @PostMapping(path = "/getProcess")
    public Flux<?> getProcess(@RequestBody ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String compCode = filter.getCompCode();
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        Integer deptId = filter.getDeptId();
        boolean deleted = filter.isDeleted();
        boolean finished = filter.isFinished();
        String processNo = Util1.isNull(filter.getProcessNo(), "-");
        String pt = filter.getVouStatus();
        return Flux.empty();
    }

    @PostMapping(path = "/saveProcessDetail")
    public Mono<ProcessHisDetail> saveProcessDetail(@RequestBody ProcessHisDetail p) {
        return processHisService.update(p);
    }


    @GetMapping(path = "/getProcessDetail")
    public Flux<ProcessHisDetail> getProcessDetail(@RequestParam String vouNo,
                                                   @RequestParam String compCode) {
        return processHisService.search(vouNo, compCode);
    }
}
