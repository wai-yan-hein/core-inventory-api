/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.StockInOutDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author Lenovo
 */
@Repository
public class StockInOutDetailDaoImpl extends AbstractDao<String, StockInOutDetail> implements StockInOutDetailDao {

    @Override
    public StockInOutDetail save(StockInOutDetail stock) {
        persist(stock);
        return stock;
    }

    @Override
    public int delete(String code) {
        String hsql = "delete from StockInOut o where o.id = '" + code + "'";
        return execUpdateOrDelete(hsql);
    }

    @Override
    public List<StockInOutDetail> search(String vouNo) {
        String hsql = "select o from StockInOutDetail o where o.ioKey.vouNo ='" + vouNo + "'";
        return findHSQL(hsql);

    }

}
