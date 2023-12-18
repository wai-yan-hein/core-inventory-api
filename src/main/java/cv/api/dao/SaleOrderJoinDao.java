package cv.api.dao;

import cv.api.entity.SaleOrderJoin;
import cv.api.entity.SaleOrderJoinKey;

import java.util.List;

public interface SaleOrderJoinDao {
    SaleOrderJoin save(SaleOrderJoin obj);
    void deleteOrder(SaleOrderJoinKey key);
    List<SaleOrderJoin> getSaleOrder(String saleVouNo,String compCode);
}
