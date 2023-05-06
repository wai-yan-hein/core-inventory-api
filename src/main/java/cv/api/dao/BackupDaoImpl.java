package cv.api.dao;

import cv.api.entity.BKSaleHis;
import cv.api.entity.BKSaleHisDetail;
import cv.api.entity.BKSaleHisKey;
import org.springframework.stereotype.Repository;

@Repository
public class BackupDaoImpl extends AbstractDao<BKSaleHisKey, Object> implements BackupDao {

    @Override
    public void exeSql(String... str) {
        execSql(str);
    }

    @Override
    public BKSaleHis save(BKSaleHis bk) {
        saveOrUpdate(bk,bk.getKey());
        return bk;
    }

    @Override
    public BKSaleHisDetail save(BKSaleHisDetail bkd) {
        //saveOrUpdate(bkd,bkd.getSdKey());
        return bkd;
    }
}
