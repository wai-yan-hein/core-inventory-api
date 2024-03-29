/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.Category;
import cv.api.entity.CategoryKey;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
public interface CategoryDao {

    Category findByCode(CategoryKey key);

    Category save(Category item);

    List<Category> findAll(String compCode, Integer deptId);

    List<Category> search(String catName);

    List<Category> unUpload();

    int delete(String id);

    LocalDateTime getMaxDate();

    List<Category> getCategory(LocalDateTime updatedDate);

}
