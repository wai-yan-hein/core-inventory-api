/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.MillingRawDetail;
import cv.api.entity.MillingRawDetailKey;

import java.util.List;

/**
 * @author wai yan
 */
public interface MillingRawDao {

    MillingRawDetail save(MillingRawDetail sdh);

    List<MillingRawDetail> search(String vouNo, String compCode, Integer deptId);

    List<MillingRawDetail> searchDetail(String vouNo, String compCode, Integer deptId);

    int delete(MillingRawDetailKey key);


}
