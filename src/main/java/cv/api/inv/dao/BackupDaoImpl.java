package cv.api.inv.dao;

import cv.api.inv.entity.BKSaleHis;
import cv.api.inv.entity.BKSaleHisDetail;
import org.springframework.stereotype.Repository;

@Repository
public class BackupDaoImpl extends AbstractDao<String, Object> implements BackupDao {

    @Override
    public void exeSql(String... str) {
        execSQL(str);
    }

    @Override
    public BKSaleHis save(BKSaleHis bk) {
        persist(bk);
        return  bk;
    }

    @Override
    public BKSaleHisDetail save(BKSaleHisDetail bkd) {
        persist(bkd);
        return bkd;
    }
}