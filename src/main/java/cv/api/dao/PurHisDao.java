/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.PurHis;
import cv.api.entity.PurHisKey;
import cv.api.model.VDescription;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
public interface PurHisDao {

    PurHis save(PurHis ph);

    List<PurHis> search(String fromDate, String toDate, String cusCode,
                        String vouNo, String remark, String userCode);

    PurHis findById(PurHisKey id);

    void delete(PurHisKey key);

    void restore(PurHisKey key) throws Exception;


    List<PurHis> unUploadVoucher(LocalDateTime syncDate);

    List<PurHis> unUpload(String syncDate);

    List<PurHis> search(String updatedDate, List<String> keys);
    List<VDescription> getDescription(String str, String compCode, String tranType);
}
