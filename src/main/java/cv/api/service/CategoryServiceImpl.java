/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.CategoryDao;
import cv.api.entity.Category;
import cv.api.entity.CategoryKey;
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
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryDao dao;

    @Autowired
    private SeqTableService seqService;

    @Override
    public Category save(Category cat) {
        if (Util1.isNull(cat.getKey().getCatCode())) {
            Integer macId = cat.getMacId();
            String compCode = cat.getKey().getCompCode();
            String catCode = getCatCode(macId, compCode);
            cat.getKey().setCatCode(catCode);

        }

        return dao.save(cat);
    }

    @Override
    public List<Category> findAll(String compCode, Integer deptId) {
        return dao.findAll(compCode, deptId);
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    @Override
    public List<Category> search(String catName) {
        return dao.search(catName);
    }

    @Override
    public List<Category> unUpload() {
        return dao.unUpload();
    }

    @Override
    public LocalDateTime getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<Category> getCategory(LocalDateTime updatedDate) {
        return dao.getCategory(updatedDate);
    }

    private String getCatCode(Integer macId, String compCode) {
        int seqNo = seqService.getSequence(macId, "Category", "-", compCode);
        return String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 4 + "d", seqNo);
    }

    @Override
    public Category findByCode(CategoryKey key) {
        return dao.findByCode(key);
    }

}
