package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.common.Util1;
import cv.api.entity.TransferHis;
import cv.api.entity.WeightHis;
import cv.api.entity.WeightHisKey;
import cv.api.model.VTransfer;
import cv.api.service.WeightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/weight")
@Slf4j
@RequiredArgsConstructor
public class WeightController {
    private final WeightService weightService;

    @PostMapping(path = "/saveWeight")
    public Mono<?> saveWeight(@RequestBody WeightHis obj) {
        obj = weightService.save(obj);
        return Mono.justOrEmpty(obj);
    }

    @PostMapping(path = "/findWeight")
    public Mono<?> saveWeight(@RequestBody WeightHisKey key) {
        return Mono.justOrEmpty(weightService.findById(key));
    }

    @PostMapping(path = "/deleteWeight")
    public Mono<?> deleteWeight(@RequestBody WeightHisKey key) {
        weightService.delete(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/restoreWeight")
    public Mono<?> restoreWeight(@RequestBody WeightHisKey key) {
        weightService.restore(key);
        return Mono.just(true);
    }
    @PostMapping(path = "/getWeightHistory")
    public Flux<?> getWeightHistory(@RequestBody FilterObject filter) throws Exception {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String refNo = Util1.isNull(filter.getRefNo(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String compCode = filter.getCompCode();
        Integer deptId = filter.getDeptId();
        String deleted = String.valueOf(filter.isDeleted());
        String traderCode = Util1.isNull(filter.getCusCode(), "-");
        return Flux.empty();
    }

}
