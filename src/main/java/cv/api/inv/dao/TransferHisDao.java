/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.TransferHis;

import java.util.List;

/**
 * @author wai yan
 */
 public interface TransferHisDao {

     TransferHis save(TransferHis ph);

     TransferHis findById(String id);

     List<TransferHis> search(String from, String to, String location,
            String remark, String vouNo);
    
    

     int delete(String vouNo);
}
