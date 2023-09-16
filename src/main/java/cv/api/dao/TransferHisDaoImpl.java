package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.TransferHis;
import cv.api.entity.TransferHisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@Slf4j
public class TransferHisDaoImpl extends AbstractDao<TransferHisKey, TransferHis> implements TransferHisDao {
    @Autowired
    private TransferHisDetailDao dao;

    @Override
    public TransferHis save(TransferHis th) {
        saveOrUpdate(th, th.getKey());
        return th;
    }

    @Override
    public TransferHis findById(TransferHisKey key) {
        TransferHis byKey = getByKey(key);
        if (byKey != null) {
            byKey.setVouDateTime(Util1.toZonedDateTime(byKey.getVouDate()));
        }
        return byKey;
    }

    @Override
    public List<TransferHis> unUpload(String syncDate) {
        String hsql = "select o from TransferHis o where o.intgUpdStatus is null and date(o.vouDate) >= '" + syncDate + "'";
        List<TransferHis> list = findHSQL(hsql);
        list.forEach((o) -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer depId = o.getDeptId();
            o.setListTD(dao.search(vouNo, compCode, depId));
        });
        return list;
    }

    @Override
    public void delete(TransferHisKey key) {
        TransferHis th = findById(key);
        th.setDeleted(true);
        th.setUpdatedDate(LocalDateTime.now());
        update(th);
    }

    @Override
    public void restore(TransferHisKey key) {
        TransferHis th = findById(key);
        th.setDeleted(false);
        th.setUpdatedDate(LocalDateTime.now());
        update(th);
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from transfer_his";
        ResultSet rs = getResult(sql);
        try {
            if (rs.next()) {
                Date date = rs.getTimestamp("date");
                if (date != null) {
                    return date;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Util1.getSyncDate();
    }

    @Override
    public List<TransferHis> search(String updatedDate, List<String> location) {
        List<TransferHis> list = new ArrayList<>();
        if (location != null) {
            for (String locCode : location) {
                //vou_no, created_by, created_date, deleted, vou_date, ref_no, remark, updated_by,
                // updated_date, loc_code_from, loc_code_to, mac_id, comp_code, dept_id, intg_upd_status
                String sql = "select * from transfer_his where (loc_code_from ='" + locCode + "' or loc_code_to ='" + locCode + "') and intg_upd_status is null";
                try {
                    ResultSet rs = getResult(sql);
                    if (rs != null) {
                        while (rs.next()) {
                            TransferHis th = new TransferHis();
                            TransferHisKey key = new TransferHisKey();
                            key.setVouNo(rs.getString("vou_no"));
                            key.setCompCode(rs.getString("comp_code"));
                            th.setKey(key);
                            th.setDeptId(rs.getInt("dept_id"));
                            th.setCreatedBy(rs.getString("created_by"));
                            th.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
                            th.setDeleted(rs.getBoolean("deleted"));
                            th.setVouDate(rs.getTimestamp("vou_date").toLocalDateTime());
                            th.setRefNo(rs.getString("ref_no"));
                            th.setRemark(rs.getString("remark"));
                            th.setUpdatedBy(rs.getString("updated_by"));
                            th.setUpdatedDate(rs.getTimestamp("updated_date").toLocalDateTime());
                            th.setLocCodeFrom(rs.getString("loc_code_from"));
                            th.setLocCodeTo(rs.getString("loc_code_to"));
                            th.setMacId(rs.getInt("mac_id"));
                            th.setIntgUpdStatus(rs.getString("intg_upd_status"));
                            list.add(th);
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        list.forEach(o -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer deptId = o.getDeptId();
            o.setListTD(dao.searchDetail(vouNo, compCode, deptId));
        });
        return list;
    }

    @Override
    public void truncate(TransferHisKey key) {
    }
}
