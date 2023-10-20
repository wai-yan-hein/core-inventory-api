/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.StockUnit;
import cv.api.entity.StockUnitKey;
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
public class StockUnitDaoImpl extends AbstractDao<StockUnitKey, StockUnit> implements StockUnitDao {

    @Override
    public StockUnit save(StockUnit item) {
        item.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(item, item.getKey());
        return item;
    }

    @Override
    public List<StockUnit> findAll(String compCode, Integer deptId) {
        String hsql = "select o from StockUnit o where o.key.compCode = '" + compCode + "' and (o.deptId =" + deptId + " or 0=" + deptId + ")";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        return 1;
    }

    @Override
    public StockUnit findByCode(StockUnitKey code) {
        return getByKey(code);
    }

    @Override
    public List<StockUnit> unUpload() {
        String hsql = "select o from StockUnit o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from stock_unit";
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
    public List<StockUnit> getUnit(LocalDateTime updatedDate) {
        String hsql = "select o from StockUnit o where o.updatedDate >:updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }
}
