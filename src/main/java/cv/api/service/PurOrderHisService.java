/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.PurOrderHisDetail;
import cv.api.entity.PurOrderHisDetailKey;
import cv.api.entity.PurOrderHis;
import cv.api.entity.PurOrderHisKey;

import java.util.List;

/**
 * @author pann
 */
public interface PurOrderHisService {

    PurOrderHis save(PurOrderHis obj);

    PurOrderHis findById(PurOrderHisKey key);

    boolean delete(PurOrderHisKey key);

    boolean restore(PurOrderHisKey key);

    PurOrderHisDetail save(PurOrderHisDetail obj);

    boolean delete(PurOrderHisDetailKey key);
    List<PurOrderHisDetail> getPurOrderHisDetail(String vouNo,String compCode);

}
