package cv.api.service;

import cv.api.common.FilterObject;
import cv.api.entity.PaymentHis;
import cv.api.entity.PaymentHisDetail;
import cv.api.entity.PaymentHisKey;
import cv.api.model.VSale;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentHisService {
    PaymentHis save(PaymentHis obj);

    PaymentHis find(PaymentHisKey key);

    void delete(PaymentHisKey key);

    void restore(PaymentHisKey key);

    Flux<PaymentHis> search(FilterObject filter);

    List<PaymentHis> unUploadVoucher(LocalDateTime syncDate);

    List<VSale> getPaymentVoucher(String vouNo, String compCode);

    boolean checkPaymentExists(String vouNo, String traderCode, String compCode, String tranOption);
    Flux<PaymentHisDetail> getTraderBalance(String traderCode, String tranOption, String compCode);

}
