package cv.api.controller;

import cv.api.dto.YearEnd;
import cv.api.service.YearEndService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final YearEndService yearEndService;
    @PostMapping(path = "/yearEnd")
    public Mono<YearEnd> yearEnd(@RequestBody YearEnd yearEnd) {
        return yearEndService.yearEnd(yearEnd);
    }
}
