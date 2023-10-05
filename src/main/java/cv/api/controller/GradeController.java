package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.common.Util1;
import cv.api.entity.GRN;
import cv.api.entity.GRNKey;
import cv.api.entity.GradeHis;
import cv.api.entity.GradeHisKey;
import cv.api.service.GRNDetailService;
import cv.api.service.GRNService;
import cv.api.service.GradeService;
import cv.api.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/grade")
@Slf4j
public class GradeController {
    @Autowired
    private GradeService gradeService;
    @Autowired
    private ReportService reportService;

    @PostMapping
    public Mono<?> saveGrade(@RequestBody GradeHis g) {
        return Mono.justOrEmpty(gradeService.save(g));
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
//        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
//        String batchNo = Util1.isNull(filter.getBatchNo(), "-");
        Integer deptId = filter.getDeptId();
        String deleted = String.valueOf(filter.isDeleted());
        String close = String.valueOf(filter.isClose());
//        boolean orderByBatch = filter.isOrderByBatch();
        List<GradeHis> list = reportService.getGradeHistory(fromDate, toDate,  traderCode, vouNo, remark, userCode, stockCode,  compCode, deptId, deleted, close);
        return Flux.fromIterable(list).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getGradeDetail")
    public Flux<?> getGradeDetail(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer deptId) {
        return Flux.fromIterable(gradeService.searchDetail(vouNo, compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }

//    @GetMapping(path = "/getGRNDetailBatch")
//    public Flux<?> getGRNDetailBatch(@RequestParam String batchNo, @RequestParam String compCode, @RequestParam Integer deptId) {
//        List<GRN> list = grnService.search(batchNo, compCode, deptId);
//        if (!list.isEmpty()) {
//            String vouNo = list.get(0).getKey().getVouNo();
//            return Flux.fromIterable(grnDetailService.search(vouNo, compCode, deptId)).onErrorResume(throwable -> Flux.empty());
//        }
//        return Flux.fromIterable(new ArrayList<>());
//    }

    @PostMapping(path = "/deleteGrade")
    public Mono<?> deleteGrade(@RequestBody GradeHisKey key) {
        return Mono.justOrEmpty(gradeService.delete(key));
    }

    @PostMapping(path = "/restoreGrade")
    public Mono<?> restoreGrade(@RequestBody GradeHisKey key) {
        return Mono.justOrEmpty(gradeService.restore(key));
    }

    @PostMapping(path = "/openGrade")
    public Mono<?> openGrade(@RequestBody GradeHisKey key) {
        return Mono.justOrEmpty(gradeService.delete(key));
    }
    @PostMapping(path = "/findGrade")
    public Mono<?> findGrade(@RequestBody GradeHisKey key) {
        return Mono.justOrEmpty(gradeService.findByCode(key));
    }
//    @GetMapping(path = "/getBatchList")
//    public Flux<?> getBatchList(@RequestParam String batchNo, @RequestParam String compCode, @RequestParam Integer deptId) {
//        return Flux.fromIterable(grnService.search(Util1.cleanStr(batchNo), compCode, deptId)).onErrorResume(throwable -> Flux.empty());
//    }

//    @GetMapping(path = "/findByBatchNo")
//    public Mono<?> findByBatchNo(@RequestParam String batchNo, @RequestParam String compCode, @RequestParam Integer deptId) {
//        return Mono.justOrEmpty(grnService.findByBatchNo(batchNo, compCode, deptId));
//    }
}
