package cv.api.inv.dao;

import cv.api.inv.entity.BKSaleHis;
import cv.api.inv.entity.BKSaleHisDetail;

public interface BackupDao {
    void exeSql(String... str);

    BKSaleHis save(BKSaleHis bk);

    BKSaleHisDetail save(BKSaleHisDetail bkd);
}
