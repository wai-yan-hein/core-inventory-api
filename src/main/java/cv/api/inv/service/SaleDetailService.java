/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.SaleHis;
import cv.api.inv.entity.SaleHisDetail;

import java.util.List;

/**
 * @author wai yan
 */
public interface SaleDetailService {

    SaleHisDetail save(SaleHisDetail sdh);

    List<SaleHisDetail> search(String vouNo);

    void save(SaleHis saleHis, List<SaleHisDetail> listSaleDetail,
              String vouStatus, List<String> deleteList) throws Exception;

    void saveH2(SaleHis saleHis, List<SaleHisDetail> listSaleDetail,
                String vouStatus, List<String> deleteList) throws Exception;

    int delete(String code);
}
