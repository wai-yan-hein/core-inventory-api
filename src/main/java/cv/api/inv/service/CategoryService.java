/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.Category;
import cv.api.inv.entity.StockBrand;

import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
 public interface CategoryService {

     Category findByCode(String code);

     Category save(Category category) throws Exception;

     List<Category> findAll(String compCode, Integer deptId);

     int delete(String id);

     List<Category> search(String catName);

    List<Category> unUpload();

    Date getMaxDate();

    List<Category> getCategory(String updatedDate);
}
