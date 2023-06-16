/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.StockType;
import cv.api.entity.StockTypeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Repository
@Slf4j
public class StockTypeDaoImpl extends AbstractDao<StockTypeKey, StockType> implements StockTypeDao {

    @Override
    public StockType save(StockType item) {
        saveOrUpdate(item,item.getKey());
        return item;
    }

    @Override
    public List<StockType> findAll(String compCode, Integer deptId) {
        String hsql = "select o from StockType o where o.key.compCode = '" + compCode + "' and (o.key.deptId=" + deptId + " or 0 =" + deptId + ")";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
     return 1;
    }

    @Override
    public StockType findByCode(StockTypeKey key) {
        return getByKey(key);
    }

    @Override
    public List<StockType> unUpload() {
        String hsql = "select o from StockType o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from stock_type";
        ResultSet rs = getResult(sql);
        try {
            if (rs.next()) {
                Date date = rs.getTimestamp("date");
                if (date != null) {
                    return date;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Util1.getOldDate();
    }

    @Override
    public List<StockType> getStockType(LocalDateTime updatedDate) {
        String hsql = "select o from StockType o where o.updatedDate > :updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }

}
