/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.Location;
import cv.api.entity.LocationKey;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
public interface LocationService {

    Location findByCode(LocationKey code);

    Location save(Location loc);

    List<Location> findAll(String compCode, String whCode);

    List<Location> findAll();

    int delete(String id);

    List<Location> search(String parent);

    List<Location> unUpload();

    List<Location> getLocation(LocalDateTime updatedDate);

    Mono<Boolean> insertTmp(List<String> listStr, String compCode,Integer macId, String warehouse);
}
