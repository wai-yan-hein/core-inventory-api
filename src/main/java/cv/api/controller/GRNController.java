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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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
    public Flux<?> getHistory(@RequestBody FilterObject filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String compCode = filter.getCompCode();
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String batchNo = Util1.isNull(filter.getBatchNo(), "-");
        Integer deptId = filter.getDeptId();
        String deleted = String.valueOf(filter.isDeleted());
        String close = String.valueOf(filter.isClose());
        boolean orderByBatch = filter.isOrderByBatch();
        List<GRN> list = reportService.getGRNHistory(fromDate, toDate, batchNo, traderCode,
                vouNo, remark, userCode, stockCode,
                locCode, compCode, deptId, deleted,
                close, orderByBatch);
        return Flux.fromIterable(list);
    }

    @GetMapping(path = "/get-grn-detail")
    public Flux<?> getGRNDetail(@RequestParam String vouNo,
                                @RequestParam String compCode,
                                @RequestParam Integer deptId) {
        return Flux.fromIterable(grnDetailService.search(vouNo, compCode, deptId));
    }

    @GetMapping(path = "/get-grn-detail-batch")
    public Flux<?> getGRNDetailBatch(@RequestParam String batchNo,
                                     @RequestParam String compCode,
                                     @RequestParam Integer deptId) {
        List<GRN> list = grnService.search(batchNo, compCode, deptId);
        if (!list.isEmpty()) {
            String vouNo = list.get(0).getKey().getVouNo();
            return Flux.fromIterable(grnDetailService.search(vouNo, compCode, deptId));
        }
        return Flux.fromIterable(new ArrayList<>());
    }

    @PostMapping(path = "/delete-grn")
    public Mono<?> deleteGRN(@RequestBody GRNKey key) {
        grnService.delete(key);
        return Mono.just(true);
    }

    @GetMapping(path = "/get-batch-list")
    public ResponseEntity<?> findByBatch(@RequestParam String batchNo,
                                         @RequestParam String compCode,
                                         @RequestParam Integer deptId) {
        return ResponseEntity.ok(grnService.search(Util1.cleanStr(batchNo), compCode, deptId));
    }
}
