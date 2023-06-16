/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.Category;
import cv.api.entity.CategoryKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface CategoryService {

    Category findByCode(CategoryKey key);

    Category save(Category category);

    List<Category> findAll(String compCode, Integer deptId);

    int delete(String id);

    List<Category> search(String catName);

    List<Category> unUpload();

    LocalDateTime getMaxDate();

    List<Category> getCategory(String updatedDate);
}
