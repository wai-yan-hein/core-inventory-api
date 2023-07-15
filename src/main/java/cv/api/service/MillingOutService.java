/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.MillingOutDetail;
import cv.api.entity.MillingOutDetailKey;

import java.util.List;

/**
 * @author wai yan
 */
public interface MillingOutService {

    MillingOutDetail save(MillingOutDetail sdh);

    List<MillingOutDetail> search(String vouNo, String compCode, Integer deptId);

    int delete(MillingOutDetailKey key);


}
