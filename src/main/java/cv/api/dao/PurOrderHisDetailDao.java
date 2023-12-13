package cv.api.dao;

import cv.api.entity.PurOrderHisDetail;
import cv.api.entity.PurOrderHisDetailKey;

import java.util.List;

public interface PurOrderHisDetailDao {
    PurOrderHisDetail save(PurOrderHisDetail obj);

    boolean delete(PurOrderHisDetailKey key);
    boolean deletePurOrderHisDetail(String vouNo,String compCode);
    List<PurOrderHisDetail> getPurOrderHisDetail(String vouNo, String compCode);

}
