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

    public Category save(Category category);

    public List<Category> findAll();

    public int delete(String id);

    public List<Category> search(String catName);

    public List<Category> searchM(String updatedDate);

}
