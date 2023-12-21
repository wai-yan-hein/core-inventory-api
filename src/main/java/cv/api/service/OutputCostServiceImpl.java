/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.OutputCostDao;
import cv.api.entity.OutputCost;
import cv.api.entity.OutputCostKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class OutputCostServiceImpl implements OutputCostService {

    @Autowired
    private OutputCostDao dao;

    @Autowired
    private SeqTableService seqService;

    @Override
    public OutputCost save(OutputCost cat) {
        if (Util1.isNull(cat.getKey().getOutputCostCode())) {
            String compCode = cat.getKey().getCompCode();
            String catCode = getCode(compCode);
            cat.getKey().setOutputCostCode(catCode);

        }

        return dao.save(cat);
    }

    @Override
    public List<OutputCost> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public int delete(OutputCostKey key) {
        return dao.delete(key);
    }

    @Override
    public List<OutputCost> search(String catName) {
        return dao.search(catName);
    }

    @Override
    public List<OutputCost> unUpload() {
        return dao.unUpload();
    }

    @Override
    public LocalDateTime getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<OutputCost> getOutputCost(LocalDateTime updatedDate) {
        return dao.getOutputCost(updatedDate);
    }

    private String getCode(String compCode) {
        int seqNo = seqService.getSequence(0, "OutputCost", "-", compCode);
        return String.format("%0" + 3 + "d", 0) + "-" + String.format("%0" + 4 + "d", seqNo);
    }

    @Override
    public OutputCost findByCode(OutputCostKey key) {
        return dao.findByCode(key);
    }

}
