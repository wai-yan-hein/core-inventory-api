package cv.api.dao;

import cv.api.entity.WeightHis;
import cv.api.entity.WeightHisKey;

import java.util.List;

public interface WeightDao {
    WeightHis save(WeightHis obj);



    WeightHis findById(WeightHisKey key);

    boolean delete(WeightHisKey key);

    boolean restore(WeightHisKey key);
    List<WeightHis> getWeightHistory(String fromDate, String toDate, String traderCode,
                                     String stockCode,String vouNo,String remark,
                                     boolean deleted,String compCode);

}
