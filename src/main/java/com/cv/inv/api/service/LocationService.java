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

     Location findByCode(String code);

     Location save(Location loc) throws Exception;

     List<Location> findAll(String compCode);

     int delete(String id);

     List<Location> search(String parent);

}
