package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.entity.ProcessHis;
import cv.api.entity.ProcessHisDetail;
import cv.api.entity.ProcessHisDetailKey;
import cv.api.entity.ProcessHisKey;
import cv.api.service.ProcessHisDetailService;
import cv.api.service.ProcessHisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequestMapping("/process")
@Slf4j
public class ProcessController {
    private final ReturnObject ro = new ReturnObject();
    @Autowired
    private ProcessHisService processHisService;
    @Autowired
    private ProcessHisDetailService processHisDetailService;

    @PostMapping(path = "/save-process")
    public Mono<?> saveProcess(@RequestBody ProcessHis p) {
        return Mono.just(processHisService.save(p));
    }

    @PostMapping(path = "/delete-process")
    public Mono<?> deleteProcess(@RequestBody ProcessHisKey p) {
        processHisService.delete(p);
        return Mono.just(true);
    }

    @PostMapping(path = "/find-process")
    public Mono<?> findProcess(@RequestBody ProcessHisKey p) {
        return Mono.justOrEmpty(processHisService.findById(p));
    }

    @PostMapping(path = "/restore-process")
    public Mono<?> restoreProcess(@RequestBody ProcessHisKey p) {
        processHisService.restore(p);
        return Mono.just(true);
    }

    @PostMapping(path = "/get-process")
    public Flux<?> getProcess(@RequestBody FilterObject filter) {
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
        return Flux.fromIterable(processHisService.search(fromDate, toDate, vouNo, processNo, remark, stockCode, pt, locCode, finished, deleted, compCode, deptId));
    }

    @PostMapping(path = "/save-process-detail")
    public Mono<?> saveProcessDetail(@RequestBody ProcessHisDetail p) {
        return Mono.just(processHisDetailService.save(p));
    }

    @PostMapping(path = "/delete-process-detail")
    public Mono<?> deleteProcessDetail(@RequestBody ProcessHisDetailKey p) {
        processHisDetailService.delete(p);
        ro.setMessage("Deleted.");
        return Mono.just(true);
    }

    @GetMapping(path = "/get-process-detail")
    public Flux<?> getProcessDetail(@RequestParam String vouNo,
                                              @RequestParam String compCode,
                                              @RequestParam Integer deptId) {
        return Flux.fromIterable(processHisDetailService.search(vouNo, compCode, deptId));
    }
}
