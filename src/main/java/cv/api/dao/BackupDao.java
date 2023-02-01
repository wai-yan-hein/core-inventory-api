package cv.api.dao;

import cv.api.entity.BKSaleHis;
import cv.api.entity.BKSaleHisDetail;

public interface BackupDao {
    void exeSql(String... str);

    BKSaleHis save(BKSaleHis bk);

    BKSaleHisDetail save(BKSaleHisDetail bkd);
}
