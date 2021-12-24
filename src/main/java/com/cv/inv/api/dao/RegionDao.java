/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.Region;

import java.util.List;

/**
 *
 * @author WSwe
 */
 public interface RegionDao {

     Region save(Region region);

     Region findByCode(String id);

     List<Region> search(String code, String name, String compCode, String parentCode);

     int delete(String code);

     List<Region> findAll(String compCode);
}
