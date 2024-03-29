/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.VouStatus;
import cv.api.entity.VouStatusKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface VouStatusDao {

    VouStatus save(VouStatus vouStatus);

    List<VouStatus> findAll(String compCode);

    int delete(String id);

    VouStatus findById(VouStatusKey id);

    List<VouStatus> search(String des);

    List<VouStatus> unUpload();


    List<VouStatus> getVouStatus(LocalDateTime updatedDate);


}
