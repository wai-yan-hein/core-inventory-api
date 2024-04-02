/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.Location;
import cv.api.entity.LocationKey;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
public interface LocationDao {

    Location save(Location loc);

    List<Location> findAll(String compCode, String whCode);

    List<Location> findAll();

    int delete(String id);

    Location findByCode(LocationKey code);

    List<Location> search(String parent);

    List<Location> unUpload();
    

    List<Location> getLocation(LocalDateTime updatedDate);

}
