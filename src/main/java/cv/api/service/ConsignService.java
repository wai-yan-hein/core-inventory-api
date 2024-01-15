/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.*;

import java.util.List;

/**
 * @author pann
 */
public interface ConsignService {

    ConsignHis save(ConsignHis obj);

    ConsignHis findById(ConsignHisKey key);

    boolean delete(ConsignHisKey key);

    boolean restore(ConsignHisKey key);

    ConsignHisDetail save(ConsignHisDetail obj);

    boolean delete(ConsignHisDetailKey key);
    List<ConsignHisDetail> getStockIssRecDetail(String vouNo, String compCode);

}
