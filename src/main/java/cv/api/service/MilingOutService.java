/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.MilingOutDetail;
import cv.api.entity.MilingOutDetailKey;
import cv.api.entity.SaleDetailKey;
import cv.api.entity.SaleHisDetail;

import java.util.List;

/**
 * @author wai yan
 */
public interface MilingOutService {

    MilingOutDetail save(MilingOutDetail sdh);

    List<MilingOutDetail> search(String vouNo, String compCode, Integer deptId);

    int delete(MilingOutDetailKey key);

    List<MilingOutDetail> getSaleByBatch(String batchNo, String compCode, Integer depId);

    List<MilingOutDetail> getSaleByBatchDetail(String batchNo, String compCode, Integer depId);


}
