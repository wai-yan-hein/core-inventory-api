/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.StockInOutDetail;
import cv.api.entity.StockInOutKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * @author wai yan
 */
@Repository
@Slf4j
public class StockInOutDetailDaoImpl extends AbstractDao<StockInOutKey, StockInOutDetail> implements StockInOutDetailDao {

    @Override
    public StockInOutDetail save(StockInOutDetail stock) {
        saveOrUpdate(stock, stock.getKey());
        return stock;
    }

    @Override
    public int delete(StockInOutKey key) {
        remove(key);
        return 1;
    }



}
