/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.RetInHis;
import com.cv.inv.api.entity.RetInHisDetail;
import java.util.List;

/**
 *
 * @author lenovo
 */
public interface RetInService {

    public void save(RetInHis retIn, List<RetInHisDetail> listRetIn, List<String> delList);

    public RetInHis saveM(RetInHis retIn);

    public void delete(String retInId) throws Exception;

    public List<RetInHis> search(String fromDate, String toDate, String cusId,
            String locId, String vouNo, String filterCode);

    public RetInHis findById(String id);
}
