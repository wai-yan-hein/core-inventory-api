/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.SaleMan;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Repository
public class SaleManDaoImpl extends AbstractDao<String, SaleMan> implements SaleManDao {

    @Override
    public SaleMan save(SaleMan saleMan) {
        persist(saleMan);
        return saleMan;
    }

    @Override
    public List<SaleMan> findAll() {
        String hsql = "select o from SaleMan o";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from SaleMan o where o.saleManId='" + id + "'";
        return execUpdateOrDelete(hsql);
    }

    @Override
    public SaleMan findById(String id) {
        return getByKey(id);
    }

}
