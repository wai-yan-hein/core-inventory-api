package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.common.Util1;
import cv.api.inv.entity.ProcessHis;
import cv.api.inv.entity.ProcessHisDetail;
import cv.api.inv.entity.ProcessHisDetailKey;
import cv.api.inv.entity.ProcessHisKey;
import cv.api.inv.service.ProcessHisDetailService;
import cv.api.inv.service.ProcessHisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/process")
@Slf4j
public class ProcessController {
    @Autowired
    private ProcessHisService processHisService;
    @Autowired
    private ProcessHisDetailService processHisDetailService;

    @PostMapping(path = "/save-process")
    public ResponseEntity<?> saveProcess(@RequestBody ProcessHis p) {
        return ResponseEntity.ok(processHisService.save(p));
    }

    @PostMapping(path = "/delete-process")
    public ResponseEntity<?> deleteProcess(@RequestBody ProcessHisKey p) {
        processHisService.delete(p);
        return ResponseEntity.ok(ResponseEntity.accepted());
    }

    @PostMapping(path = "/get-process")
    public ResponseEntity<?> getProcess(@RequestBody FilterObject filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String compCode = filter.getCompCode();
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        Integer deptId = filter.getDeptId();
        boolean deleted = filter.isDeleted();
        boolean finished = filter.isDeleted();
        String processNo = filter.getProcessNo();
        String pt = filter.getVouStatus();
        return ResponseEntity.ok(processHisService.search(fromDate, toDate, vouNo, processNo, remark, stockCode, pt, locCode, finished, deleted, compCode, deptId));
    }

    @PostMapping(path = "/save-process-detail")
    public ResponseEntity<?> saveProcessDetail(@RequestBody ProcessHisDetail p) {
        return ResponseEntity.ok(processHisDetailService.save(p));
    }

    @PostMapping(path = "/delete-process-detail")
    public ResponseEntity<?> deleteProcessDetail(@RequestBody ProcessHisDetailKey p) {
        processHisDetailService.delete(p);
        return ResponseEntity.ok(ResponseEntity.accepted());
    }

    @PostMapping(path = "/get-process-detail")
    public ResponseEntity<?> getProcessDetail(@RequestBody FilterObject f) {
        String compCode = f.getCompCode();
        Integer depId = f.getDeptId();
        String vouNo = f.getVouNo();
        String stockCode = f.getStockCode();
        return ResponseEntity.ok(processHisDetailService.search(vouNo, stockCode, compCode, depId));
    }
}
