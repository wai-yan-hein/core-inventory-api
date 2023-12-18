/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.StockCriteria;
import cv.api.entity.StockCriteriaKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wai yan
 */
@Repository
@Slf4j
public class StockCriteriaDaoImpl extends AbstractDao<StockCriteriaKey, StockCriteria> implements StockCriteriaDao {

    @Override
    public StockCriteria save(StockCriteria item) {
        item.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(item, item.getKey());
        return item;
    }

    @Override
    public List<StockCriteria> findAll(String compCode, boolean active) {
        String hsql = "select o from StockCriteria o where o.key.compCode = '" + compCode + "' and o.deleted =false";
        if (active) {
            hsql += " and active = " + true;
        }
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        return 1;
    }

    @Override
    public List<StockCriteria> search(String compCode, String text) {
        text = text + "%";
        String sql = """
                select *
                from stock_criteria
                where active = true
                and deleted = false
                and (user_code like ? or criteria_name like ?)
                and comp_code =?
                """;
        List<StockCriteria> list = new ArrayList<>();
        try {
            ResultSet rs = getResult(sql, text, text,   compCode);
            while (rs.next()) {
                StockCriteria sc = new StockCriteria();
                StockCriteriaKey key = new StockCriteriaKey();
                key.setCriteriaCode(rs.getString("criteria_code"));
                key.setCompCode(rs.getString("comp_code"));
                sc.setKey(key);
                sc.setUserCode(rs.getString("user_code"));
                sc.setCriteriaName(rs.getString("criteria_name"));
                list.add(sc);
            }
        } catch (Exception e) {
            log.error("search : " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<StockCriteria> unUpload() {
        String hsql = "select o from StockCriteria o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public StockCriteria findByCode(StockCriteriaKey key) {
        return getByKey(key);
    }

    @Override
    public LocalDateTime getMaxDate() {
        String sql = "select max(updated_date) date from stock_criteria";
        ResultSet rs = getResult(sql);
        try {
            if (rs.next()) {
                LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
                if (date != null) {
                    return date;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Util1.getOldLocalDateTime();
    }

    @Override
    public List<StockCriteria> getCriteria(LocalDateTime updatedDate) {
        String hsql = "select o from StockCriteria o where o.updatedDate >: updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }

    @Override
    public List<StockCriteria> search(String stockCode, String stockType, String cat, String brand, String compCode, boolean orderFavorite) {
        String hsql = "select o from Stock o where o.active = true and o.deleted = false and o.key.compCode ='" + compCode + "'\n";
        if (!stockCode.equals("-")) {
            hsql += " and o.key.stockCode ='" + stockCode + "'\n";
        }
        if (!stockType.equals("-")) {
            hsql += " and o.typeCode ='" + stockType + "'\n";
        }
        if (!cat.equals("-")) {
            hsql += " and o.catCode ='" + cat + "'\n";
        }
        if (!brand.equals("-")) {
            hsql += " and o.brandCode ='" + brand + "'\n";
        }
        if (orderFavorite) {
            hsql += " order by o.favorite desc,o.userCode";
        } else {
            hsql += " order by o.userCode";
        }
        return findHSQL(hsql);
    }
}
