/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.SaleHisDetail;

import java.util.List;

/**
 * @author wai yan
 */
 public interface SaleHisDetailDao {

    SaleHisDetail save(SaleHisDetail sdh);

    List<SaleHisDetail> search(String vouNo, String compCode, Integer deptId);

    List<SaleHisDetail> searchDetail(String vouNo, String compCode, Integer deptId);

    int delete(String code, String compCode, Integer deptId);
}
