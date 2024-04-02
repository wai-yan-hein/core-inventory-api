package cv.api.report;


import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.repo.LogRepo;
import cv.api.repo.UserRepo;
import cv.api.report.model.StockValueDto;
import cv.api.report.model.TopPurchase;
import cv.api.service.LocationService;
import cv.api.service.OPHisService;
import cv.api.service.ReportR2dbcService;
import cv.api.service.StockReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryUpload {
    private final LogRepo logRepo;
    private final UserRepo userRepo;
    private final StockReportService stockReportService;
    private final OPHisService opHisService;
    private final LocationService locationService;
    private final ReportR2dbcService reportService;

    @Scheduled(fixedRate = 5 * 60 * 1000)
    private void autoUpload() {
        userRepo.createWebClient();
        userRepo.getCompanySync().doOnSuccess(list -> list.forEach(company -> {
            String url = company.getReportUrl();
            String compCode = company.getCompCode();
            String headCode = company.getReportCompany();
            log.info("url : " + url);
            log.info("companyId : " + headCode);
            logRepo.createWebClient(url);
            uploadStockValue(headCode, compCode);
            uploadPurchase(headCode, compCode);
        })).onErrorResume(e -> {
            log.error("getCompanySync : " + e.getMessage());
            return Mono.empty();
        }).subscribe();
    }

    private void uploadStockValue(String headCode, String compCode) {
        opHisService.getOpeningDateByLocation(compCode, "-")
                .flatMap(opDate -> locationService.insertTmp(null, compCode, 100, "-")
                        .flatMap(aBoolean -> {
                            var filter = ReportFilter.builder()
                                    .macId(100)
                                    .compCode(compCode)
                                    .opDate(opDate)
                                    .toDate(Util1.toDateStr(LocalDate.now(), "yyyy-MM-dd"))
                                    .build();
                            return stockReportService.getStockBalanceByLocation(filter)
                                    .map(v -> StockValueDto.builder()
                                            .tranDate(LocalDate.now())
                                            .compCode(headCode)
                                            .groupName(v.getStockTypeName())
                                            .catName(v.getCatName())
                                            .stockCode(v.getStockUserCode())
                                            .stockName(v.getStockName())
                                            .locName(v.getLocName())
                                            .qty(v.getQty())
                                            .bag(v.getBag())
                                            .build())
                                    .collectList()
                                    .flatMap(logRepo::stockValue)
                                    .doOnSuccess(success -> log.info("uploadStockValue"))
                                    .onErrorResume(throwable -> {
                                        log.error("uploadStockValue : " + throwable.getMessage());
                                        return Mono.empty();
                                    });
                        }))
                .subscribe();
    }

    private void uploadPurchase(String headCode, String compCode) {
        reportService.getTopPurchaseList(toDayDate(), toDayDate(), compCode, "-", "-", "-", "-", "-")
                .flatMap(list -> Flux.fromIterable(list)
                        .map(p -> TopPurchase.builder()
                                .tranDate(LocalDate.now())
                                .compCode(headCode)
                                .catName(p.getGroupName())
                                .stockCode(p.getStockUserCode())
                                .stockName(p.getStockName())
                                .wet(p.getAvgWet())
                                .rice(p.getAvgRice())
                                .amount(p.getVouTotal())
                                .qty(p.getQty())
                                .price(p.getAvgPrice())
                                .qtyPercent(p.getQtyPercent())
                                .build())
                        .collectList()
                ).flatMap(logRepo::topPurchase)
                .doOnSuccess(success -> log.info("uploadPurchase"))
                .onErrorResume(throwable -> {
                    log.error("uploadPurchase : " + throwable.getMessage());
                    return Mono.empty();
                }).subscribe();
    }


    private String toDayDate() {
        LocalDate today = LocalDate.now();
        // Format today's date as "yyyy-MM-dd"
        return today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }


}
