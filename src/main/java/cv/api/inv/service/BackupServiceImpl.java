package cv.api.inv.service;

import cv.api.common.Util1;
import cv.api.inv.dao.BackupDao;
import cv.api.inv.dao.SaleHisDao;
import cv.api.inv.dao.SaleHisDetailDao;
import cv.api.inv.entity.BKSaleHis;
import cv.api.inv.entity.BKSaleHisDetail;
import cv.api.inv.entity.SaleHis;
import cv.api.inv.entity.SaleHisDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class BackupServiceImpl implements BackupService {
    @Autowired
    private BackupDao dao;
    @Autowired
    private SaleHisDao shDao;
    @Autowired
    private SaleHisDetailDao shdDao;

    @Override
    public void backup(SaleHis sh) {
        /*if (sh.getStatus().equals("EDIT")) {
            if (sh.isBackup()) {
                SaleHis s = shDao.findById(sh.getKey());
                BKSaleHis bk = (BKSaleHis) Util1.cast(s, BKSaleHis.class);
                bk = dao.save(bk);
                List<SaleHisDetail> sd = shdDao.search(sh.getKey().getVouNo(), sh.getKey().getCompCode(), sh.getKey().getDeptId());
                for (SaleHisDetail b : sd) {
                    BKSaleHisDetail bks = (BKSaleHisDetail) Util1.cast(b, BKSaleHisDetail.class);
                    bks.setLogId(bk.getKey().getLogId());
                    dao.save(bks);
                }
                log.info("backup sale.");
            }
        }*/
    }
}
