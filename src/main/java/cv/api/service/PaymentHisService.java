package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.entity.PaymentHis;
import cv.api.entity.PaymentHisDetail;
import cv.api.model.VSale;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface PaymentHisService {
    Mono<PaymentHis> save(PaymentHis obj);

    Mono<PaymentHis> find(String vouNo, String compCode);

    Mono<Boolean> delete(String vouNo, String compCode);

    Mono<Boolean> restore(String vouNo, String compCode);

    Flux<PaymentHis> search(ReportFilter filter);

    Flux<PaymentHis> unUploadVoucher(LocalDateTime syncDate);

    Flux<VSale> getPaymentVoucher(String vouNo, String compCode);

    Flux<PaymentHisDetail> getTraderBalance(String traderCode, String tranOption, String compCode);
    Mono<PaymentHis> getTraderBalanceSummary(String traderCode, String tranOption, String compCode);
    Flux<PaymentHisDetail> getPaymentDetail(String vouNo,String compCode);

}
