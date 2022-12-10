/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.PurHisDetail;

import java.util.List;

/**
 * @author wai yan
 */
public interface PurHisDetailDao {

    PurHisDetail save(PurHisDetail pd);

    List<PurHisDetail> search(String vouNo, String compCode, Integer deptId);

    int delete(String id, String compCode, Integer deptId) throws Exception;

}
