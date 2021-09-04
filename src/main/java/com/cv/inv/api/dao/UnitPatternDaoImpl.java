/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.UnitPattern;

import java.util.List;

import org.springframework.stereotype.Repository;

/**
 * @author Lenovo
 */
@Repository
public class UnitPatternDaoImpl extends AbstractDao<Integer, UnitPattern> implements UnitPatternDao {

    @Override
    public UnitPattern save(UnitPattern unit) {
        persist(unit);
        return unit;
    }

    @Override
    public List<UnitPattern> findAll() {
        String hsql = "select o from UnitPattern o ";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String delSql = "delete from unit_pattern where patter_id = " + id + " ";
        return execUpdateOrDelete(delSql);
    }

}
