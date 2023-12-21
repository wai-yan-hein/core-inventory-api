package cv.api.dao;

import cv.api.entity.WeightHisDetail;
import cv.api.entity.WeightHisDetailKey;
import cv.api.model.WeightColumn;

import java.util.List;

public interface WeightDetailDao {
    WeightHisDetail save(WeightHisDetail obj);

    boolean delete(WeightHisDetailKey key);
    boolean deleteWeightHisDetail(String vouNo,String compCode);
    List<WeightHisDetail> getWeightDetail(String vouNo, String compCode);
    List<WeightColumn> getWeightColumn(String vouNo,String compCode);


}
