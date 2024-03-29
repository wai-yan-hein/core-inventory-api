package cv.api.service;


import cv.api.entity.WareHouse;
import cv.api.entity.WareHouseKey;

import java.time.LocalDateTime;
import java.util.List;

public interface WareHouseService {
    WareHouse save(WareHouse status);
    List<WareHouse> findAll(String compCode);
    int delete(WareHouseKey key);
    WareHouse findById(WareHouseKey key);
    List<WareHouse> getWarehouse(LocalDateTime updatedDate);
}
