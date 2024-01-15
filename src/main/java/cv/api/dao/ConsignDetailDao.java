package cv.api.dao;

import cv.api.entity.ConsignHisDetail;
import cv.api.entity.ConsignHisDetailKey;

import java.util.List;

public interface ConsignDetailDao {
    ConsignHisDetail save(ConsignHisDetail obj);

    boolean delete(ConsignHisDetailKey key);
    boolean deleteStockIssRecDetail(String vouNo,String compCode);
    List<ConsignHisDetail> getStockIssRecDetail(String vouNo, String compCode);

}
