package cv.api.dao;

import cv.api.entity.StockIssRecDetail;
import cv.api.entity.StockIssRecDetailKey;

import java.util.List;

public interface StockIssRecDetailDao {
    StockIssRecDetail save(StockIssRecDetail obj);

    boolean delete(StockIssRecDetailKey key);
    boolean deleteStockIssRecDetail(String vouNo,String compCode);
    List<StockIssRecDetail> getStockIssRecDetail(String vouNo, String compCode);

}
