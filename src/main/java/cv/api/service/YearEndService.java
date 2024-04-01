package cv.api.service;


import cv.api.dto.YearEnd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class YearEndService {
    private final StockService stockService;
    private final StockTypeService stockTypeService;
    private final CategoryService categoryService;

    public Mono<YearEnd> yearEnd(YearEnd end) {
        return copyStock(end)
                .then(copyStockGroup(end))
                .then(copyCategory(end))
                .thenReturn(end);
    }

    private Mono<Boolean> copyStock(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        log.info("copyStock.");
        return stockService.findActiveStock(compCode, 0)
                .flatMap(d -> {
                    d.getKey().setCompCode(yeCompCode);
                    return stockService.insert(d);
                }).then(Mono.just(true));
    }

    private Mono<Boolean> copyStockGroup(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        log.info("copyStockGroup.");
        return stockTypeService.findAllActive(compCode)
                .flatMap(d -> {
                    d.getKey().setCompCode(yeCompCode);
                    return stockTypeService.insert(d);
                }).then(Mono.just(true));
    }

    private Mono<Boolean> copyCategory(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        log.info("copyCategory.");
        return categoryService.findAllActive(compCode)
                .flatMap(d -> {
                    d.getKey().setCompCode(yeCompCode);
                    return categoryService.insert(d);
                }).then(Mono.just(true));
    }
}
