package cv.api.dao;

import cv.api.entity.GRN;
import cv.api.entity.GRNKey;

import java.util.List;

public interface GRNDao {
    GRN findByCode(GRNKey key);

    GRN save(GRN b);

    List<GRN> findAll(String compCode, Integer deptId);

    boolean delete(GRNKey key);
    boolean restore(GRNKey key);

    List<GRN> search(String batchNo, String compCode, Integer deptId);
    boolean open(GRNKey key);

}
