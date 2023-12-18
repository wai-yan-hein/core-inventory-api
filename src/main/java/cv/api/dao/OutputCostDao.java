/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.OutputCost;
import cv.api.entity.OutputCostKey;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
public interface OutputCostDao {

    OutputCost findByCode(OutputCostKey key);

    OutputCost save(OutputCost item);

    List<OutputCost> findAll(String compCode);

    List<OutputCost> search(String catName);

    List<OutputCost> unUpload();

    int delete(OutputCostKey key);

    LocalDateTime getMaxDate();

    List<OutputCost> getOutputCost(LocalDateTime updatedDate);

}
