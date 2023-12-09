package cv.api.dao;

import cv.api.entity.StockIssueReceive;
import cv.api.entity.StockIssueReceiveKey;
import cv.api.model.VStockIssueReceive;

import java.util.List;

public interface StockIssRecDao {
    StockIssueReceive save(StockIssueReceive obj);

    StockIssueReceive findById(StockIssueReceiveKey key);

    boolean delete(StockIssueReceiveKey key);

    boolean restore(StockIssueReceiveKey key);
}
