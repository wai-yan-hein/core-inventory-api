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
public interface WeightService {

    WeightHis save(WeightHis obj);

    WeightHis findById(WeightHisKey key);

    boolean delete(WeightHisKey key);

    boolean restore(WeightHisKey key);
    WeightHisDetail save(WeightHisDetail obj);
    boolean delete(WeightHisDetailKey key);
    List<WeightHis> getWeightHistory(String fromDate,String toDate,String traderCode,String stockCode,
                                     String vouNo,String remark,
                                     boolean deleted,String compCode,String transSource);
    List<WeightHisDetail> getWeightDetail(String vouNo,String compCode);

}
