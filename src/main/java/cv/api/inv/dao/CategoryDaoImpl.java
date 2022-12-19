/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.common.Util1;
import cv.api.inv.entity.Category;
import cv.api.inv.entity.CategoryKey;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Repository
@Slf4j
public class CategoryDaoImpl extends AbstractDao<CategoryKey, Category> implements CategoryDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Category save(Category item) {
        persist(item);
        return item;
    }

    @Override
    public List<Category> findAll(String compCode, Integer deptId) {
        String hsql = "select o from Category o where o.key.compCode = '" + compCode + "' and (o.key.deptId =" + deptId + " or 0 = " + deptId + ")";
        Query<Category> query = sessionFactory.getCurrentSession().createQuery(hsql, Category.class);
        return query.list();
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from Category o where o.catCode ='" + id + "'";
        return execUpdateOrDelete(hsql);
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
        Query<Category> query = sessionFactory.getCurrentSession().createQuery(strFilter, Category.class);
        return query.list();
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
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from category";
        ResultSet rs = getResultSet(sql);
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
    public List<Category> getCategory(String updatedDate) {
        String hsql = "select o from Category o where o.updatedDate > '" + updatedDate + "'";
        return findHSQL(hsql);
    }
}
