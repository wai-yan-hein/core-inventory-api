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

    public Location save(Location loc);

    public List<Location> findAll(String compCode);

    public int delete(String id);

    public Location findById(String id);

    public List<Location> search(String parent);

}
