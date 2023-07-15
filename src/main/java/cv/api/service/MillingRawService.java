/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.MillingRawDetail;
import cv.api.entity.MillingRawDetailKey;

import java.util.List;

/**
 * @author wai yan
 */
public interface MillingRawService {

    MillingRawDetail save(MillingRawDetail sdh);

    List<MillingRawDetail> search(String vouNo, String compCode, Integer deptId);

    int delete(MillingRawDetailKey key);

}
