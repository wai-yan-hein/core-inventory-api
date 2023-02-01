/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.StockInOutDetail;

import java.util.List;

/**
 * @author wai yan
 */
public interface StockInOutDetailDao {

    StockInOutDetail save(StockInOutDetail sdh);

    List<StockInOutDetail> search(String vouNo, String compCode, Integer deptId);

    int delete(String id, String compCode, Integer deptId);

}
