/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.Category;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class CategoryDaoImpl extends AbstractDao<String, Category> implements CategoryDao {


    @Override
    public Category save(Category item) {
        persist(item);
        return item;
    }

    @Override
    public List findAll(String compCode) {
        String hsql = "select o from Category o where o.compCode = '" + compCode + "'";
        return findHSQL(hsql);
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

        return (List<Category>) findHSQL(strFilter);
    }

    @Override
    public List<Category> searchM(String updatedDate) {
        String strSql = "select o from Category o where o.updatedDate > '" + updatedDate + "'";
        return (List<Category>) findHSQL(strSql);
    }

    @Override
    public Category findByCode(String code) {
        return getByKey(code);
    }

}
