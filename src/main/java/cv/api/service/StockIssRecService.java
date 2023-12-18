/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.*;
import cv.api.model.VStockIssueReceive;
import java.util.List;

/**
 * @author pann
 */
public interface StockIssRecService {

    StockIssueReceive save(StockIssueReceive obj);

    StockIssueReceive findById(StockIssueReceiveKey key);

    boolean delete(StockIssueReceiveKey key);

    boolean restore(StockIssueReceiveKey key);

    StockIssRecDetail save(StockIssRecDetail obj);

    boolean delete(StockIssRecDetailKey key);
    List<StockIssRecDetail> getStockIssRecDetail(String vouNo,String compCode);

}
