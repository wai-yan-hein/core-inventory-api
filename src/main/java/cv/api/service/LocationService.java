/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.Location;
import cv.api.entity.LocationKey;

import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface LocationService {

    Location findByCode(LocationKey code);

    Location save(Location loc);

    List<Location> findAll(String compCode, Integer deptId);

    List<Location> findAll();

    int delete(String id);

    List<Location> search(String parent);

    List<Location> unUpload();

    Date getMaxDate();

    List<Location> getLocation(String updatedDate);

    List<String> getLocation(Integer deptId);
}
