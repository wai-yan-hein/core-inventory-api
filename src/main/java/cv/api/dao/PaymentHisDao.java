package cv.api.dao;

import cv.api.entity.PaymentHis;
import cv.api.entity.PaymentHisKey;
import cv.api.model.VSale;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentHisDao {
    PaymentHis save(PaymentHis obj);

    PaymentHis find(PaymentHisKey key);
    void restore(PaymentHisKey key);

    void delete(PaymentHisKey key);
    List<PaymentHis> unUploadVoucher(LocalDateTime syncDate);
    List<VSale> getPaymentVoucher(String vouNo, String compCode);
    boolean checkPaymentExists(String vouNo, String traderCode, String compCode, String tranOption);


}
