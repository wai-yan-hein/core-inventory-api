/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.Location;
import java.util.List;

/**
 *
 * @author Lenovo
 */
 public interface LocationDao {

     Location save(Location loc);

     List<Location> findAll(String compCode);

     int delete(String id);

     Location findByCode(String code);

     List<Location> search(String parent);

}
