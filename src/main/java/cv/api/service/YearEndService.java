package cv.api.service;


import cv.api.dto.YearEnd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class YearEndService {
    private final StockService stockService;
    private final StockTypeService stockTypeService;
    private final CategoryService categoryService;
    private final StockBrandService brandService;
    private final UnitRelationService relationService;
    private final TraderService traderService;
    private final StockUnitService unitService;

    public Mono<YearEnd> yearEnd(YearEnd end) {
        return copyStock(end)
                .then(copyStockGroup(end))
                .then(copyCategory(end))
                .then(copyBrand(end))
                .then(copyRelation(end))
                .then(copyUnit(end))
                .then(copyTrader(end))
                .thenReturn(end);
    }

    private Mono<Boolean> copyUnit(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        log.info("copyUnit.");
        return unitService.findAll(yeCompCode)
                .concatMap(d -> {
                    d.getKey().setCompCode(compCode);
                    return unitService.insert(d);
                }).then(Mono.just(true));
    }

    private Mono<Boolean> copyStock(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        log.info("copyStock.");
        return stockService.findActiveStock(yeCompCode)
                .concatMap(d -> {
                    d.getKey().setCompCode(compCode);
                    return stockService.insert(d);
                }).then(Mono.just(true));
    }

    private Mono<Boolean> copyStockGroup(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        log.info("copyStockGroup.");
        return stockTypeService.findAllActive(yeCompCode)
                .concatMap(d -> {
                    d.getKey().setCompCode(compCode);
                    return stockTypeService.insert(d);
                }).then(Mono.just(true));
    }

    private Mono<Boolean> copyCategory(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        log.info("copyCategory.");
        return categoryService.findAllActive(yeCompCode)
                .concatMap(d -> {
                    d.getKey().setCompCode(compCode);
                    return categoryService.insert(d);
                }).then(Mono.just(true));
    }

    private Mono<Boolean> copyBrand(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        log.info("copyBrand.");
        return brandService.findAllActive(yeCompCode)
                .concatMap(d -> {
                    d.getKey().setCompCode(compCode);
                    return brandService.insert(d);
                }).then(Mono.just(true));
    }

    private Mono<Boolean> copyRelation(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        log.info("copyRelation.");
        return relationService.getUnitRelationAndDetail(yeCompCode)
                .concatMap(d -> {
                    d.getKey().setCompCode(compCode);
                    return relationService.insert(d).flatMap(t -> Flux.fromIterable(t.getDetailList())
                            .flatMap(detail -> {
                                detail.getKey().setCompCode(compCode);
                                return relationService.insert(detail);
                            })
                            .then()).thenReturn(end);
                })
                .then(Mono.just(true));
    }

    private Mono<Boolean> copyTrader(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        log.info("copyTrader.");
        return traderService.findAllActive(yeCompCode)
                .concatMap(d -> {
                    d.getKey().setCompCode(compCode);
                    return traderService.insert(d);
                }).then(Mono.just(true));
    }

}
