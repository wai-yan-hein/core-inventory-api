package cv.api.dao;

import cv.api.entity.SaleOrderJoin;
import cv.api.entity.SaleOrderJoinKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SaleOrderJoinDaoImpl extends AbstractDao<SaleOrderJoinKey, SaleOrderJoin> implements SaleOrderJoinDao {
    @Override
    public SaleOrderJoin save(SaleOrderJoin obj) {
        saveOrUpdate(obj, obj.getKey());
        return obj;
    }

    @Override
    public void deleteOrder(SaleOrderJoinKey key) {
        remove(key);
    }

    @Override
    public List<SaleOrderJoin> getSaleOrder(String saleVouNo, String compCode) {
        String hsql = "select o from SaleOrderJoin o where o.key.compCode ='" + compCode + "' and o.key.saleVouNo = '" + saleVouNo + "'";
        return findHSQL(hsql);
    }
}
