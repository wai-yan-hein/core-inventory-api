/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.PurHisDetail;

import java.util.List;

/**
 * @author wai yan
 */
 public interface PurHisDetailService {

     PurHisDetail save(PurHisDetail pd);
    
     List<PurHisDetail> search(String glCode);

     int delete(String code) throws Exception;

}
