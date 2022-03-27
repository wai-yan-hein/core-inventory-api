/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.RetInHis;
import cv.api.inv.view.VReturnIn;

import java.util.List;

/**
 * @author wai yan
 */
public interface RetInService {

    RetInHis save(RetInHis ri) throws Exception;

    RetInHis update(RetInHis ri);

    List<RetInHis> search(String fromDate, String toDate, String cusCode,
                          String vouNo, String remark, String userCode);

    RetInHis findById(String id);

    int delete(String vouNo) throws Exception;

    List<VReturnIn> search(String vouNo);
}
