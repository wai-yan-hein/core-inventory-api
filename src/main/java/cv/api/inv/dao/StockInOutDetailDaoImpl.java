/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.StockInOutDetail;
import cv.api.inv.entity.StockInOutKey;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
public class StockInOutDetailDaoImpl extends AbstractDao<StockInOutKey, StockInOutDetail> implements StockInOutDetailDao {

    @Override
    public StockInOutDetail save(StockInOutDetail stock) {
        persist(stock);
        return stock;
    }

    @Override
    public int delete(String code) {
        String delSql = "delete from stock_in_out_detail  where sd_code = '" + code + "'";
        execSQL(delSql);
        return 1;
    }

    @Override
    public List<StockInOutDetail> search(String vouNo) {
        String hsql = "select o from StockInOutDetail o where o.ioKey.vouNo ='" + vouNo + "' order by o.uniqueId";
        return findHSQL(hsql);

    }

}
