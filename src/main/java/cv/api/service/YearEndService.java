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
    private final LocationService locationService;
    private final VouStatusService vouStatusService;

    public Mono<YearEnd> yearEnd(YearEnd end) {
        return copyStock(end)
                .then(copyStockGroup(end))
                .then(copyCategory(end))
                .then(copyBrand(end))
                .then(copyRelation(end))
                .then(copyUnit(end))
                .then(copyTrader(end))
                .then(copyLocation(end))
                .then(copyVouStatus(end))
                .thenReturn(end);
    }

    private Mono<Boolean> copyUnit(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        return unitService.isExist(yeCompCode).flatMap(exist -> {
            if (exist) {
                log.info("unit exist.");
                return Mono.just(true);
            }
            return unitService.findAll(yeCompCode)
                    .concatMap(d -> {
                        d.getKey().setCompCode(compCode);
                        return unitService.insert(d);
                    }).doOnComplete(() -> log.info("copyUnit."))
                    .then(Mono.just(true));
        });
    }

    private Mono<Boolean> copyStock(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        return stockService.isExist(yeCompCode)
                .flatMap(exist -> {
                    if (exist) {
                        log.info("stock exist.");
                        return Mono.just(true);
                    }
                    return stockService.findActiveStock(yeCompCode)
                            .concatMap(d -> {
                                d.getKey().setCompCode(compCode);
                                return stockService.insert(d);
                            }).doOnComplete(() -> log.info("copyStock."))
                            .then(Mono.just(true));
                });
    }

    private Mono<Boolean> copyStockGroup(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        return stockTypeService.isExist(yeCompCode)
                .flatMap(exist -> {
                    if (exist) {
                        log.info("stock group exist.");
                        return Mono.just(true);
                    }
                    return stockTypeService.findAllActive(yeCompCode)
                            .concatMap(d -> {
                                d.getKey().setCompCode(compCode);
                                return stockTypeService.insert(d);
                            }).doOnComplete(() -> log.info("copyStockGroup."))
                            .then(Mono.just(true));
                });
    }

    private Mono<Boolean> copyCategory(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        return categoryService.isExist(yeCompCode)
                .flatMap(exist -> {
                    if (exist) {
                        log.info("category exists.");
                        return Mono.just(true);
                    }
                    return categoryService.findAllActive(yeCompCode)
                            .concatMap(d -> {
                                d.getKey().setCompCode(compCode);
                                return categoryService.insert(d);
                            }).doOnComplete(() -> log.info("copyCategory."))
                            .then(Mono.just(true));
                });
    }

    private Mono<Boolean> copyBrand(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        return brandService.isExist(yeCompCode)
                .flatMap(exist -> {
                    if (exist) {
                        log.info("brand exist.");
                        return Mono.just(true);
                    }
                    return brandService.findAllActive(yeCompCode)
                            .concatMap(d -> {
                                d.getKey().setCompCode(compCode);
                                return brandService.insert(d);
                            }).doOnComplete(() -> log.info("copyBrand."))
                            .then(Mono.just(true));
                });
    }

    private Mono<Boolean> copyRelation(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        return relationService.isExist(yeCompCode)
                .flatMap(exist -> {
                    if (exist) {
                        log.info("relation exist.");
                        return Mono.just(true);
                    }
                    return relationService.getUnitRelationAndDetail(yeCompCode)
                            .concatMap(d -> {
                                d.getKey().setCompCode(compCode);
                                return relationService.insert(d).flatMap(t -> Flux.fromIterable(t.getDetailList())
                                        .flatMap(detail -> {
                                            detail.getKey().setCompCode(compCode);
                                            return relationService.insert(detail);
                                        })
                                        .then()).thenReturn(end);
                            }).doOnComplete(() -> log.info("copyRelation."))
                            .then(Mono.just(true));
                });
    }

    private Mono<Boolean> copyTrader(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        return traderService.isExist(yeCompCode).flatMap(exist -> {
            if (exist) {
                log.info("trader exist.");
                return Mono.just(true);
            }
            return traderService.findAllActive(yeCompCode)
                    .concatMap(d -> {
                        d.getKey().setCompCode(compCode);
                        return traderService.insert(d);
                    }).doOnComplete(() -> log.info("copyTrader."))
                    .then(Mono.just(true));
        });
    }

    private Mono<Boolean> copyLocation(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        return locationService.isExist(yeCompCode)
                .flatMap(exist -> {
                    if (exist) {
                        log.info("location exist.");
                        return Mono.just(true);
                    }
                    return locationService.findAll(yeCompCode, "-")
                            .concatMap(d -> {
                                d.getKey().setCompCode(compCode);
                                return locationService.insert(d);
                            })
                            .doOnComplete(() -> log.info("copyLocation."))
                            .then(Mono.just(true));
                });
    }

    private Mono<Boolean> copyVouStatus(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        return vouStatusService.isExist(yeCompCode).flatMap(exist -> {
            if (exist) {
                log.info("vou status exist.");
                return Mono.just(true);
            }
            return vouStatusService.findAll(yeCompCode)
                    .concatMap(d -> {
                        d.getKey().setCompCode(compCode);
                        return vouStatusService.insert(d);
                    })
                    .doOnComplete(() -> log.info("copyVouStatus."))
                    .then(Mono.just(true));
        });

    }
}
