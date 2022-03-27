/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.Region;

import java.util.List;

/**
 *
 * @author WSwe
 */
 public interface RegionService {

     Region save(Region region) throws Exception;

     Region findByCode(String id);

     List<Region> search(String code, String name, String compCode, String parentCode);

     int delete(String code);

     List<Region> findAll(String compCode);
}
