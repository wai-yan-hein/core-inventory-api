/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.CategoryDao;
import com.cv.inv.api.entity.Category;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
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
        if (cat.getCatCode() == null || cat.getCatCode().isEmpty()) {
            Integer macId = cat.getMacId();
            String compCode = cat.getCompCode();
            String getCode=getCatCode(macId, "Category", "-", compCode);
            cat.setCatCode(getCode);
        }
        return dao.save(cat);
    }

    @Override
    public List<Category> findAll() {
        return dao.findAll();
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
    public List<Category> searchM(String updatedDate) {
        return dao.searchM(updatedDate);
    }

    private String getCatCode(Integer macId, String option, String period, String compCode) {

        int seqNo = seqService.getSequence(macId, option, period, compCode);

        String tmpCatCode = String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 4 + "d", seqNo);
        return tmpCatCode;
    }

}
