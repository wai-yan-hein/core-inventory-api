/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.VouStatus;
import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
 public interface VouStatusDao {

     VouStatus save(VouStatus vouStatus);

     List<VouStatus> findAll();

     int delete(String id);

     VouStatus findById(String id);
     List<VouStatus> search(String statusDesp);

}
