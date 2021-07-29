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

    public Category findByCode(String code);

    public Category save(Category category) throws Exception;

    public List<Category> findAll(String compCode);

    public int delete(String id);

    public List<Category> search(String catName);

    public List<Category> searchM(String updatedDate);

}
