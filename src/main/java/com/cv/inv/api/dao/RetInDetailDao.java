/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.RetInHisDetail;

import java.util.List;

/**
 *
 * @author Lenovo
 */
 public interface RetInDetailDao {

     RetInHisDetail save(RetInHisDetail pd);

     List<RetInHisDetail> search(String glCode);

     int delete(String id) throws Exception;

}
