/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.SaleHis;
import com.cv.inv.api.entity.SaleHisDetail;
import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
public interface SaleDetailService {

    public SaleHisDetail save(SaleHisDetail sdh);

    public List<SaleHisDetail> search(String vouId);

    public void save(SaleHis saleHis, List<SaleHisDetail> listSaleDetail,
            String vouStatus, List<String> deleteList) throws Exception;
    
       public void saveH2(SaleHis saleHis, List<SaleHisDetail> listSaleDetail,
            String vouStatus, List<String> deleteList) throws Exception;
}
