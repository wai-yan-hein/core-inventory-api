package cv.api.inv.dao;

import cv.api.common.Util1;
import cv.api.inv.entity.TransferHis;
import cv.api.inv.entity.TransferHisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
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
        persist(th);
        return th;
    }

    @Override
    public TransferHis findById(TransferHisKey key) {
        return getByKey(key);
    }

    @Override
    public List<TransferHis> unUpload(String syncDate) {
        String hsql = "select o from TransferHis o where o.intgUpdStatus is null and date(o.vouDate) >= '" + syncDate + "'";
        List<TransferHis> list = findHSQL(hsql);
        list.forEach((o) -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer depId = o.getKey().getDeptId();
            o.setListTD(dao.search(vouNo, compCode, depId));
        });
        return list;
    }

    @Override
    public void delete(TransferHisKey key) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        Integer deptId = key.getDeptId();
        String sql = "update transfer_his set deleted =1 where vou_no ='" + vouNo + "' and comp_code='" + compCode + "' and dept_id =" + deptId + "";
        execSQL(sql);
    }

    @Override
    public void restore(TransferHisKey key) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        Integer deptId = key.getDeptId();
        String sql = "update transfer_his set deleted =0 where vou_no ='" + vouNo + "' and comp_code='" + compCode + "' and dept_id =" + deptId + "";
        execSQL(sql);
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from transfer_his";
        ResultSet rs = getResultSet(sql);
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
                String sql = "select * from transfer_his where (loc_code_from ='" + locCode + "' or loc_code_to ='" + locCode + "') and updated_date >'" + updatedDate + "'";
                try {
                    ResultSet rs = getResultSet(sql);
                    if (rs != null) {
                        while (rs.next()) {
                            TransferHis th = new TransferHis();
                            TransferHisKey key = new TransferHisKey();
                            key.setVouNo(rs.getString("vou_no"));
                            key.setDeptId(rs.getInt("dept_id"));
                            key.setCompCode(rs.getString("comp_code"));
                            th.setKey(key);
                            th.setCreatedBy(rs.getString("created_by"));
                            th.setCreatedDate(rs.getTimestamp("created_date"));
                            th.setDeleted(rs.getBoolean("deleted"));
                            th.setVouDate(rs.getDate("vou_date"));
                            th.setRefNo(rs.getString("ref_no"));
                            th.setRemark(rs.getString("remark"));
                            th.setUpdatedBy(rs.getString("updated_by"));
                            th.setUpdatedDate(rs.getTimestamp("updated_date"));
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
            Integer deptId = o.getKey().getDeptId();
            o.setListTD(dao.searchDetail(vouNo, compCode, deptId));
        });
        return list;
    }
}
