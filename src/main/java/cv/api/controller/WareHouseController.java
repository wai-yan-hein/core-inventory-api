package cv.api.controller;

import cv.api.common.Util1;
import cv.api.entity.LabourGroup;
import cv.api.entity.LabourGroupKey;
import cv.api.entity.WareHouse;
import cv.api.entity.WareHouseKey;
import cv.api.service.LabourGroupService;
import cv.api.service.WareHouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/warehouse")
public class WareHouseController {
    private final WareHouseService wareHouseService;

    @PostMapping(path = "/saveWareHouse")
    public Mono<?> saveWareHouse(@RequestBody WareHouse wareHouse) {
        return Mono.justOrEmpty(wareHouseService.save(wareHouse));
    }

    @GetMapping(path = "/getWareHouse")
    public Flux<?> getWareHouse(@RequestParam String compCode) {
        return Flux.fromIterable(wareHouseService.findAll(compCode));
    }
    @PostMapping(path = "/findWareHouse")
    public Mono<?> findWareHouse(@RequestBody WareHouseKey key) {
        return Mono.justOrEmpty(wareHouseService.findById(key));
    }
    @GetMapping(path = "/getUpdatedWarehouse")
    public Flux<?> getUpdateOutputCost(@RequestParam String updatedDate) {
        return Flux.fromIterable(wareHouseService.getWarehouse(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }

}
