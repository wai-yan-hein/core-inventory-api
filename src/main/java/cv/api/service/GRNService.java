package cv.api.service;

import cv.api.entity.GRN;
import cv.api.entity.GRNKey;

import java.util.List;

public interface GRNService {
    GRN findByCode(GRNKey key);

    GRN save(GRN b);

    List<GRN> findAll(String compCode, Integer deptId);

    List<GRN> search(String batchNo, String compCode, Integer deptId);

    boolean delete(GRNKey key);
    boolean restore(GRNKey key);

    boolean open(GRNKey key);
}
