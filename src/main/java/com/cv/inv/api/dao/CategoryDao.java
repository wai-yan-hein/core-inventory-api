/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.Category;
import java.util.List;

/**
 *
 * @author Lenovo
 */
 public interface CategoryDao {

     Category findByCode(String code);

     Category save(Category item);

     List<Category> findAll(String compCode);

     List<Category> search(String catName);

     List<Category> searchM(String updatedDate);

     int delete(String id);
}
