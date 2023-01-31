package cv.api.inv.dao;

import cv.api.inv.entity.GRN;
import cv.api.inv.entity.GRNKey;

import java.util.List;

public interface GRNDao {
    GRN findByCode(GRNKey key);

    GRN save(GRN b);

    List<GRN> findAll(String compCode, Integer deptId);

    void delete(GRNKey key);
}
