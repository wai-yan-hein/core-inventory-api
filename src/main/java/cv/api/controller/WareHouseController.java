package cv.api.controller;

import cv.api.common.Util1;
import cv.api.entity.WareHouse;
import cv.api.entity.WareHouseKey;
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
    public Mono<WareHouse> saveWareHouse(@RequestBody WareHouse dto) {
        return wareHouseService.save(dto);
    }

    @GetMapping(path = "/getWareHouse")
    public Flux<WareHouse> getWareHouse(@RequestParam String compCode) {
        return wareHouseService.findAll(compCode);
    }

    @PostMapping(path = "/findWareHouse")
    public Mono<WareHouse> findWareHouse(@RequestBody WareHouseKey key) {
        return wareHouseService.findById(key);
    }

    @GetMapping(path = "/getUpdatedWarehouse")
    public Flux<WareHouse> getUpdateOutputCost(@RequestParam String updatedDate) {
        return wareHouseService.getWarehouse(Util1.toLocalDateTime(updatedDate));
    }

}
