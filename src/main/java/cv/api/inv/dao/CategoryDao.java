/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.Category;

import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
 public interface CategoryDao {

     Category findByCode(String code);

     Category save(Category item);

     List<Category> findAll(String compCode, Integer deptId);

     List<Category> search(String catName);

    List<Category> unUpload();

    int delete(String id);

    Date getMaxDate();
}
