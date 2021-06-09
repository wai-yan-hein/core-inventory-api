/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.RetOutHisDetail;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface RetOutDetailService {

    public RetOutHisDetail save(RetOutHisDetail pd);

    public List<RetOutHisDetail> search(String glCode);

    public int delete(String id) throws Exception;

    //  public void save(PurHis gl, List<PurchaseDetail> pd,List<String> delList);
}
