/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.Category;
import io.swagger.models.auth.In;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
public class CategoryDaoImpl extends AbstractDao<String, Category> implements CategoryDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Category save(Category item) {
        persist(item);
        return item;
    }

    @Override
    public List<Category> findAll(String compCode, Integer deptId) {
        String hsql = "select o from Category o where o.key.compCode = '" + compCode + "' and o.key.deptId ="+deptId+"";
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
    public List<Category> searchM(String updatedDate) {
        String strSql = "select o from Category o where o.updatedDate > '" + updatedDate + "'";
        Query<Category> query = sessionFactory.getCurrentSession().createQuery(strSql, Category.class);
        return query.list();
    }

    @Override
    public Category findByCode(String code) {
        return getByKey(code);
    }

}
