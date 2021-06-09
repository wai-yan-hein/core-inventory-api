/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.PurHis;
import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
public interface PurchaseHisService {

    public PurHis save(PurHis purHis) throws Exception;

    public List<PurHis> search(String fromDate, String toDate, String cusId, String vouStatusId, String remark);



    public PurHis findById(String id);

    public int delete(String vouNo) throws Exception;
}
