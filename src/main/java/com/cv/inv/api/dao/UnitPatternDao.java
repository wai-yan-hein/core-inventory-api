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

     UnitPattern save(UnitPattern unit);

     List<UnitPattern> findAll();

     int delete(String id);
}
