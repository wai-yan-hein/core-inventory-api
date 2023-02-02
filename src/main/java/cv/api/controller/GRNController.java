package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.entity.GRN;
import cv.api.entity.GRNKey;
import cv.api.service.GRNDetailService;
import cv.api.service.GRNService;
import cv.api.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/grn")
@Slf4j
public class GRNController {
    @Autowired
    private GRNService grnService;
    @Autowired
    private GRNDetailService grnDetailService;
    @Autowired
    private ReportService reportService;
    private final ReturnObject ro = new ReturnObject();

    @PostMapping
    public ResponseEntity<?> saveGRN(@RequestBody GRN g) {
        return ResponseEntity.ok(grnService.save(g));
    }

    @PostMapping(path = "/history")
    public ResponseEntity<?> getSale(@RequestBody FilterObject filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String compCode = filter.getCompCode();
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        Integer deptId = filter.getDeptId();
        String deleted = String.valueOf(filter.isDeleted());
        String close = String.valueOf(filter.isClose());
        List<GRN> list = reportService.getGRNHistory(fromDate, toDate, traderCode, vouNo, remark, userCode, stockCode, locCode, compCode, deptId, deleted, close);
        return ResponseEntity.ok(list);
    }

    @GetMapping(path = "/get-grn-detail")
    public ResponseEntity<?> getGRNDetail(@RequestParam String vouNo,
                                          @RequestParam String compCode,
                                          @RequestParam Integer deptId) {
        return ResponseEntity.ok(grnDetailService.search(vouNo, compCode, deptId));
    }

    @PostMapping(path = "/delete-grn")
    public ResponseEntity<?> deleteGRN(@RequestBody GRNKey key) {
        grnService.delete(key);
        ro.setMessage("deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/get-batch-list")
    public ResponseEntity<?> findByBatch(@RequestParam String batchNo,
                                         @RequestParam String compCode,
                                         @RequestParam Integer deptId) {
        return ResponseEntity.ok(grnService.search(batchNo, compCode, deptId));
    }
}
