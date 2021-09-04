/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.PurHisDetail;

import java.util.List;

/**
 *
 * @author Lenovo
 */
 public interface PurHisDetailDao {

    PurHisDetail save(PurHisDetail pd);

    List<PurHisDetail> search(String glCode);

    int delete(String id) throws Exception;

}
