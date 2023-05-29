package cv.api.dao;

import cv.api.entity.PaymentHis;
import cv.api.entity.PaymentHisKey;

import java.util.List;

public interface PaymentHisDao {
    PaymentHis save(PaymentHis obj);

    PaymentHis find(PaymentHisKey key);

    void delete(PaymentHisKey key);

    List<PaymentHis> search(String startDate, String endDate, String traderCode,
                            String curCode, String vouNo, String userCode, String account,
                            String projectNo, String remark, boolean deleted, String compCode);

}
