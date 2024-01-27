/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.Region;
import cv.api.entity.RegionKey;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author WSwe
 */
public interface RegionDao {

    Region save(Region region);

    Region findByCode(RegionKey id);

    List<Region> search(String code, String name, String compCode, String parentCode);

    int delete(RegionKey key);

    List<Region> findAll(String compCode);
    List<Region> getRegion(LocalDateTime updatedDate);


}
