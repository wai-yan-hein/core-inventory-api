/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.dao.StockBrandDao;
import cv.api.entity.StockBrand;
import cv.api.entity.StockBrandKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class StockBrandServiceImpl implements StockBrandService {

    @Autowired
    private StockBrandDao dao;

    @Autowired
    private SeqTableService seqService;

    @Override
    public StockBrand save(StockBrand sb)  {
        if (sb.getKey().getBrandCode() == null) {
            Integer macId = sb.getMacId();
            String compCode = sb.getKey().getCompCode();
            String code = getStockBrandCode(macId, compCode);
            sb.getKey().setBrandCode(code);
        }
        return dao.save(sb);
    }

    @Override
    public List<StockBrand> findAll(String compCode, Integer deptId) {
        return dao.findAll(compCode, deptId);
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    private String getStockBrandCode(Integer macId, String compCode) {

        int seqNo = seqService.getSequence(macId, "StockBrand", "-", compCode);

        return String.format("%0" + 2 + "d", macId) + "-" + String.format("%0" + 3 + "d", seqNo);
    }

    @Override
    public StockBrand findByCode(StockBrandKey code) {
        return dao.findByCode(code);
    }

    @Override
    public List<StockBrand> unUpload() {
        return dao.unUpload();
    }

    @Override
    public List<StockBrand> getBrand(LocalDateTime updatedDate) {
        return dao.getBrand(updatedDate);
    }
}
