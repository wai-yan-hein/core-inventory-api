/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.VouStatus;
import cv.api.entity.VouStatusKey;

import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface VouStatusDao {

    VouStatus save(VouStatus vouStatus);

    List<VouStatus> findAll(String compCode, Integer deptId);

    int delete(String id);

    VouStatus findById(VouStatusKey id);

    List<VouStatus> search(String des);

    List<VouStatus> unUpload();

    Date getMaxDate();

    List<VouStatus> getVouStatus(String updatedDate);


}
