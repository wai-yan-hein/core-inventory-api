/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.PurHis;
import cv.api.inv.entity.PurHisKey;
import cv.api.inv.view.VPurchase;

import java.util.List;

/**
 * @author wai yan
 */
public interface PurHisService {


    PurHis save(PurHis ph) throws Exception;

    PurHis update(PurHis ph);

    List<PurHis> search(String fromDate, String toDate, String cusCode,
                        String vouNo, String remark, String userCode);

    PurHis findById(PurHisKey id);

    void delete(PurHisKey key) throws Exception;
    void restore(PurHisKey key) throws Exception;

    List<VPurchase> search(String vouNo);

    List<PurHis> unUploadVoucher(String syncDate);

    List<PurHis> unUpload();
}
