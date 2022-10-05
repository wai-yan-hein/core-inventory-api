/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.Location;
import cv.api.inv.entity.LocationKey;

import java.util.List;

/**
 * @author wai yan
 */
 public interface LocationDao {

     Location save(Location loc);

     List<Location> findAll(String compCode, Integer deptId);

     int delete(String id);

     Location findByCode(LocationKey code);

     List<Location> search(String parent);

}
