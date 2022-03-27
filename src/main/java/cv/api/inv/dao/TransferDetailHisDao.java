/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.TransferDetailHis;

import java.util.List;

/**
 * @author wai yan
 */
 public interface TransferDetailHisDao {

     TransferDetailHis save(TransferDetailHis sdh);

     TransferDetailHis findById(Long id);

     List<TransferDetailHis> search(String saleInvId);

     int delete(String id);
}
