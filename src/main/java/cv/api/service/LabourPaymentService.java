package cv.api.service;

import cv.api.entity.LabourPayment;
import cv.api.entity.PaymentHisKey;
import cv.api.model.VSale;

import java.time.LocalDateTime;
import java.util.List;

public interface LabourPaymentService {
    LabourPayment save(LabourPayment obj);

    LabourPayment find(PaymentHisKey key);

    void delete(PaymentHisKey key);

    void restore(PaymentHisKey key);

    List<LabourPayment> search(String startDate, String endDate, String traderCode, String curCode,
                            String vouNo, String saleVouNo, String userCode, String account,
                            String projectNo, String remark, boolean deleted, String compCode, String tranOption);

    List<LabourPayment> unUploadVoucher(LocalDateTime syncDate);

    List<VSale> getPaymentVoucher(String vouNo, String compCode);

    boolean checkPaymentExists(String vouNo, String traderCode, String compCode, String tranOption);
}
