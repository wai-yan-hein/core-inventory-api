package cv.api.report;


import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.repo.LogRepo;
import cv.api.repo.UserRepo;
import cv.api.report.model.StockValueDto;
import cv.api.service.LocationService;
import cv.api.service.OPHisService;
import cv.api.service.StockReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryUpload {
    private final LogRepo logRepo;
    private final UserRepo userRepo;
    private final StockReportService stockReportService;
    private final OPHisService opHisService;
    private final LocationService locationService;

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
            uploadStockValue(headCode,compCode);
        })).onErrorResume(e -> {
            log.error("getCompanySync : " + e.getMessage());
            return Mono.empty();
        }).subscribe();
    }

    private void uploadStockValue(String headCode,String compCode) {
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


}
