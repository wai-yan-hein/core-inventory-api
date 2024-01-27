package cv.api.dao;

import cv.api.entity.WareHouse;
import cv.api.entity.WareHouseKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface WareHouseDao {
    WareHouse save(WareHouse WareHouse);

    List<WareHouse> findAll(String compCode);

    int delete(WareHouseKey key);

    WareHouse findById(WareHouseKey id);

    List<WareHouse> getWareHouse(LocalDateTime updatedDate);
}
