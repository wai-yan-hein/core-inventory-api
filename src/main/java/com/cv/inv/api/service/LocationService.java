/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.Location;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface LocationService {

    public Location findByCode(String code);

    public Location save(Location loc) throws Exception;

    public List<Location> findAll(String compCode);

    public int delete(String id);

    public List<Location> search(String parent);

}
