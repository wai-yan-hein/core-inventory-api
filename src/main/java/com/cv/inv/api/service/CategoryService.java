/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.Category;
import java.util.List;

/**
 *
 * @author Lenovo
 */
 public interface CategoryService {

     Category findByCode(String code);

     Category save(Category category) throws Exception;

     List<Category> findAll(String compCode);

     int delete(String id);

     List<Category> search(String catName);

     List<Category> searchM(String updatedDate);

}
