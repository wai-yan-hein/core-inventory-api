package cv.api.dao;

import cv.api.entity.VouDiscount;
import cv.api.entity.VouDiscountKey;

import java.util.List;

public interface VouDiscountDao {
    VouDiscount save(VouDiscount p);

    List<VouDiscount> getVoucherDiscount(String vouNo, String compCode);

    void delete(VouDiscountKey key);
    List<VouDiscount> getDescription(String str,String compCode);
}
