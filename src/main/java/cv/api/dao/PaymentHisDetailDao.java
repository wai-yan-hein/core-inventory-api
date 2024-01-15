package cv.api.dao;

import cv.api.entity.PaymentHisDetail;
import cv.api.entity.PaymentHisDetailKey;

import java.util.List;

public interface PaymentHisDetailDao {
    PaymentHisDetail save(PaymentHisDetail obj);

    PaymentHisDetail find(PaymentHisDetailKey key);

    List<PaymentHisDetail> search(String vouNo, String compCode);

    void delete(PaymentHisDetailKey key);
}
