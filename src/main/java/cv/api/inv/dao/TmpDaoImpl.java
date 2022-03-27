package cv.api.inv.dao;

import cv.api.inv.entity.TmpStockIO;
import cv.api.inv.entity.TmpStockIOKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TmpDaoImpl extends AbstractDao<TmpStockIOKey, TmpStockIO> implements TmpDao {
    @Override
    public TmpStockIO save(TmpStockIO io) {
        persist(io);
        return io;
    }

    @Override
    public List<TmpStockIO> getStockIO(String stockCode, Integer macId) {
        String hsql = "select o from TmpStockIO o where o.key.stockCode = '" + stockCode + "' and o.key.macId =" + macId + " order by o.key.tranDate,o.key.tranOption";
        return findHSQL(hsql);
    }
}
