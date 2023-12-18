/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.OutputCost;
import cv.api.entity.OutputCostKey;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
public interface OutputCostService {

    OutputCost findByCode(OutputCostKey key);

    OutputCost save(OutputCost category);

    List<OutputCost> findAll(String compCode);

    int delete(OutputCostKey key);

    List<OutputCost> search(String catName);

    List<OutputCost> unUpload();

    LocalDateTime getMaxDate();

    List<OutputCost> getOutputCost(LocalDateTime updatedDate);
}
