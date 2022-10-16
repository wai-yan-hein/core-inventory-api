/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.Pattern;
import cv.api.inv.entity.VouStatus;
import cv.api.inv.entity.VouStatusKey;
import io.swagger.models.auth.In;

import java.util.List;

/**
 * @author wai yan
 */
public interface VouStatusService {

    VouStatus save(VouStatus vouStatus);

    List<VouStatus> findAll(String compCode, Integer deptId);

    int delete(String id);

    VouStatus findById(VouStatusKey id);

    List<VouStatus> search(String description);
    List<VouStatus> unUpload();

}
