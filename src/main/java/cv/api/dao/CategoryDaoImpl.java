/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.Category;
import cv.api.entity.CategoryKey;
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
public class CategoryDaoImpl extends AbstractDao<CategoryKey, Category> implements CategoryDao {

    @Override
    public Category save(Category item) {
        saveOrUpdate(item, item.getKey());
        return item;
    }

    @Override
    public List<Category> findAll(String compCode, Integer deptId) {
        String hsql = "select o from Category o where o.key.compCode = '" + compCode + "' and (o.key.deptId =" + deptId + " or 0 = " + deptId + ")";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        return 1;
    }

    @Override
    public List<Category> search(String catName) {
        String strFilter = "";

        if (!catName.equals("-")) {
            strFilter = "o.catName like '%" + catName + "%'";
        }

        if (strFilter.isEmpty()) {
            strFilter = "select o from Category o";
        } else {
            strFilter = "select o from Category o where " + strFilter;
        }
        return findHSQL(strFilter);
    }

    @Override
    public List<Category> unUpload() {
        String hsql = "select o from Category o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public Category findByCode(CategoryKey key) {
        return getByKey(key);
    }

    @Override
    public LocalDateTime getMaxDate() {
        String sql = "select max(updated_date) date from category";
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
    public List<Category> getCategory(LocalDateTime updatedDate) {
        String hsql = "select o from Category o where o.updatedDate >: updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }
}
