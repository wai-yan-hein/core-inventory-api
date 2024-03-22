package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.OPHisDao;
import cv.api.dao.OPHisDetailDao;
import cv.api.dao.SeqTableDao;
import cv.api.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OPHisServiceImpl implements OPHisService {
    private final OPHisDao opHisDao;
    private final OPHisDetailDao opHisDetailDao;
    private final SeqTableDao seqDao;
    private final DatabaseClient client;

    @Override
    public OPHis save(OPHis op) {
        if (Util1.isNullOrEmpty(op.getKey().getVouNo())) {
            op.getKey().setVouNo(getVoucherNo(op.getDeptId(), op.getMacId(), op.getKey().getCompCode()));
        }
        List<OPHisDetail> listSD = op.getDetailList();
        List<OPHisDetailKey> listDel = op.getListDel();
        String vouNo = op.getKey().getVouNo();
        if (listDel != null) {
            listDel.forEach(opHisDetailDao::delete);
        }
        for (int i = 0; i < listSD.size(); i++) {
            OPHisDetail cSd = listSD.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                OPHisDetailKey key = new OPHisDetailKey();
                key.setCompCode(op.getKey().getCompCode());
                key.setVouNo(vouNo);
                key.setUniqueId(0);
                cSd.setDeptId(op.getDeptId());
                cSd.setKey(key);
            }
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == 0) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        OPHisDetail pSd = listSD.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                opHisDetailDao.save(cSd);
            }
        }
        opHisDao.save(op);
        op.setDetailList(listSD);
        return op;
    }

    @Override
    public OPHis findByCode(OPHisKey key) {
        return opHisDao.findByCode(key);
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "OPENING", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }

    @Override
    public List<OPHis> search(String compCode) {
        return opHisDao.search(compCode);
    }

    @Override
    public List<OPHis> unUpload() {
        return opHisDao.unUpload();
    }

    @Override
    public boolean delete(OPHisKey key) {
        return opHisDao.delete(key);
    }

    @Override
    public boolean restore(OPHisKey key) {
        return opHisDao.restore(key);
    }

    @Override
    public Mono<String> getOpeningDateByLocation(String compCode, String locCode) {
        String sql = """
                select max(op_date) op_date
                from op_his
                where deleted = false
                and comp_code =:compCode
                and (loc_code =:locCode or '-'=:locCode)
                and (tran_source=1 or tran_source=3)
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("locCode", locCode)
                .map((row) -> row.get("op_date", String.class))
                .one()
                .switchIfEmpty(Mono.defer(() -> Mono.just("1998-10-07")));
    }

}
