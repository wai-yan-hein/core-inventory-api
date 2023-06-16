/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.StockUnit;
import cv.api.entity.StockUnitKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface StockUnitService {

    StockUnit findByCode(StockUnitKey code);

    StockUnit save(StockUnit unit);

    List<StockUnit> findAll(String compCode, Integer deptId);

    List<StockUnit> unUpload();

    int delete(String id);

    Date getMaxDate();

    List<StockUnit> getUnit(LocalDateTime updatedDate);
}
