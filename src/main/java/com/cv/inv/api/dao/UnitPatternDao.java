/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.UnitPattern;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface UnitPatternDao {

    public UnitPattern save(UnitPattern unit);

    public List<UnitPattern> findAll();

    public int delete(String id);
}
