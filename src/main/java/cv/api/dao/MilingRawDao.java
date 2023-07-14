/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.MilingRawDetail;
import cv.api.entity.MilingRawDetailKey;
import cv.api.entity.SaleDetailKey;
import cv.api.entity.SaleHisDetail;

import java.util.List;

/**
 * @author wai yan
 */
public interface MilingRawDao {

    MilingRawDetail save(MilingRawDetail sdh);

    List<MilingRawDetail> search(String vouNo, String compCode, Integer deptId);

    List<MilingRawDetail> searchDetail(String vouNo, String compCode, Integer deptId);

    int delete(MilingRawDetailKey key);

    List<MilingRawDetail> getSaleByBatch(String batchNo, String compCode, Integer depId);

    List<MilingRawDetail> getSaleByBatchDetail(String batchNo, String compCode, Integer depId);


}
