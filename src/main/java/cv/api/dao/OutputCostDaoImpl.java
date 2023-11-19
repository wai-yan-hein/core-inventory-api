/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.Category;
import cv.api.entity.CategoryKey;
import cv.api.entity.OutputCost;
import cv.api.entity.OutputCostKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
@Repository
@Slf4j
public class OutputCostDaoImpl extends AbstractDao<OutputCostKey, OutputCost> implements OutputCostDao {

    @Override
    public OutputCost save(OutputCost item) {
        item.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(item, item.getKey());
        return item;
    }

    @Override
    public List<OutputCost> findAll(String compCode) {
        String hsql = "select o from OutputCost o where o.key.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public int delete(OutputCostKey key) {
        return 1;
    }

    @Override
    public List<OutputCost> search(String catName) {
        String strFilter = "";

        if (!catName.equals("-")) {
            strFilter = "o.catName like '%" + catName + "%'";
        }

        if (strFilter.isEmpty()) {
            strFilter = "select o from OutputCost o";
        } else {
            strFilter = "select o from OutputCost o where " + strFilter;
        }
        return findHSQL(strFilter);
    }

    @Override
    public List<OutputCost> unUpload() {
        String hsql = "select o from OutputCost o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public OutputCost findByCode(OutputCostKey key) {
        return getByKey(key);
    }

    @Override
    public LocalDateTime getMaxDate() {
        String sql = "select max(updated_date) date from OutputCost";
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
    public List<OutputCost> getOutputCost(LocalDateTime updatedDate) {
        String hsql = "select o from OutputCost o where o.updatedDate >: updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }
}
