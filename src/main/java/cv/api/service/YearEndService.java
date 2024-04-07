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
    private final SeqService seqService;
    private final AccSettingService accSettingService;

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
                .then(copySequence(end))
                .then(copyAccSetting(end))
                .thenReturn(end);
    }

    private Mono<Boolean> copyUnit(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        return unitService.isExist(compCode).flatMap(exist -> {
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
        return stockService.isExist(compCode)
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
        return stockTypeService.isExist(compCode)
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
        return categoryService.isExist(compCode)
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
        return brandService.isExist(compCode)
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
        return relationService.isExist(compCode)
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
        return traderService.isExist(compCode).flatMap(exist -> {
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
        return locationService.isExist(compCode)
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
        return vouStatusService.isExist(compCode).flatMap(exist -> {
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

    private Mono<Boolean> copySequence(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        return seqService.isExist(compCode).flatMap(exist -> {
            if (exist) {
                log.info("sequence exist.");
                return Mono.just(true);
            }
            return seqService.findAll(yeCompCode)
                    .concatMap(d -> {
                        d.setCompCode(compCode);
                        return seqService.insert(d);
                    })
                    .doOnComplete(() -> log.info("copySequence."))
                    .then(Mono.just(true));
        });

    }
    private Mono<Boolean> copyAccSetting(YearEnd end) {
        String compCode = end.getCompCode();
        String yeCompCode = end.getYeCompCode();
        return accSettingService.isExist(compCode).flatMap(exist -> {
            if (exist) {
                log.info("acc setting exist.");
                return Mono.just(true);
            }
            return accSettingService.findAll(yeCompCode)
                    .concatMap(d -> {
                        d.getKey().setCompCode(compCode);
                        return accSettingService.insert(d);
                    })
                    .doOnComplete(() -> log.info("copyAccSetting."))
                    .then(Mono.just(true));
        });

    }
}
