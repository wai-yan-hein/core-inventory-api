/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.SaleHis;
import cv.api.inv.view.VSale;

import java.util.List;

/**
 * @author wai yan
 */
public interface SaleHisService {

    SaleHis save(SaleHis saleHis) throws Exception;

    SaleHis update(SaleHis saleHis);

    List<SaleHis> search(String fromDate, String toDate, String cusCode,
                         String vouNo, String remark, String userCode);

    SaleHis findById(String id);

    int delete(String vouNo) throws Exception;

    List<VSale> search(String vouNo);
}
