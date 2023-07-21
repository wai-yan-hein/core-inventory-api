package cv.api.service;

import cv.api.entity.PaymentHis;
import cv.api.entity.PaymentHisKey;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentHisService {
    PaymentHis save(PaymentHis obj);

    PaymentHis find(PaymentHisKey key);

    void delete(PaymentHisKey key);

    void restore(PaymentHisKey key);

    List<PaymentHis> search(String startDate, String endDate, String traderCode, String curCode,
                            String vouNo, String saleVouNo, String userCode, String account,
                            String projectNo, String remark, boolean deleted, String compCode,String tranOption);

    List<PaymentHis> unUploadVoucher(LocalDateTime syncDate);
}
