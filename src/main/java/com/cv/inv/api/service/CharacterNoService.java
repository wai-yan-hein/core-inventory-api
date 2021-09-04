/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.CharacterNo;
import java.util.List;

/**
 *
 * @author Lenovo
 */
 public interface CharacterNoService {

     CharacterNo save(CharacterNo ch);

     List<CharacterNo> findAll();

     int delete(String id);

     CharacterNo findById(String id);
}
