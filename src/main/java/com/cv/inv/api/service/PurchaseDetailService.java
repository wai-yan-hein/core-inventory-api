/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.PurHis;
import com.cv.inv.api.entity.PurHisDetail;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface PurchaseDetailService {

    public PurHisDetail save(PurHisDetail pd);
    
    public void saveH2(PurHis pur, List<PurHisDetail> listPD, List<String> delList);
    public List<PurHisDetail> search(String glCode);

    public void save(PurHis gl, List<PurHisDetail> pd, List<String> delList);

    public int delete(String code) throws Exception;

}
