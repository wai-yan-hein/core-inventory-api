/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.General;
import cv.api.entity.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface MillingHisService {

    MillingHis save(MillingHis milling);

    MillingHis update(MillingHis milling);

    List<MillingHis> getMillingHistory(String fromDate, String toDate, String traderCode, String vouNo, String remark, String reference,
                                       String userCode, String stockCode, String locCode, String compCode,
                                       Integer deptId, boolean deleted, String projectNo, String curCode);

    MillingHis findById(MillingHisKey id);

    void delete(MillingHisKey key) throws Exception;

    void restore(MillingHisKey key) throws Exception;


    List<MillingHis> unUploadVoucher(LocalDateTime syncDate);

    List<MillingHis> unUpload(String syncDate);

    Date getMaxDate();

    void truncate(MillingHisKey key);

    General getVoucherInfo(String vouDate, String compCode, Integer depId);

    List<MillingUsage> getMillingUsage(String vouNo, String compCode);

    MillingOutDetail save(MillingOutDetail sdh);

    List<MillingOutDetail> getMillingOut(String vouNo, String compCode, Integer deptId);

    int delete(MillingOutDetailKey key);

    MillingRawDetail save(MillingRawDetail sdh);

    List<MillingRawDetail> getMillingRaw(String vouNo, String compCode, Integer deptId);

    int delete(MillingRawDetailKey key);

    MillingExpense save(MillingExpense p);

    List<MillingExpense> getMillingExpense(String vouNo, String compCode);

    void delete(MillingExpenseKey key);


}
