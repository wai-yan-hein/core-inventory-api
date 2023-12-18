/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.MillingUsage;
import cv.api.entity.MillingUsageKey;

import java.util.List;

/**
 * @author wai yan
 */
public interface MillingUsageDao {

    MillingUsage save(MillingUsage sdh);

    List<MillingUsage> getMillingUsage(String vouNo, String compCode);
    int delete(MillingUsageKey key);
}
