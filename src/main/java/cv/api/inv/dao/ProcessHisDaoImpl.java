package cv.api.inv.dao;

import cv.api.inv.entity.ProcessHis;
import cv.api.inv.entity.ProcessHisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class ProcessHisDaoImpl extends AbstractDao<ProcessHisKey, ProcessHis> implements ProcessHisDao {
    @Override
    public ProcessHis save(ProcessHis ph) {
        persist(ph);
        return ph;
    }

    @Override
    public ProcessHis findById(ProcessHisKey key) {

        return getByKey(key);
    }

    @Override
    public List<ProcessHis> search(String fromDate, String toDate, String vouNo, String processNo, String remark,
                                   String stockCode, String pt, String locCode, boolean finish, boolean deleted, String compCode, Integer deptId) {
        List<ProcessHis> list = new ArrayList<>();
        String filter = "where comp_code ='" + compCode + "'\n"
                + "and dept_id =" + deptId + "\n"
                + "and finished =" + finish + "\n"
                + "and deleted =" + deleted + "\n"
                + "and date(vou_date)>='" + fromDate + "'\n"
                + "and date(vou_date)<='" + toDate + "'\n";
        if (!vouNo.equals("-")) {
            filter += "and vou_no ='" + vouNo + "'\n";
        }
        if (!processNo.equals("-")) {
            filter += "and process_no ='" + processNo + "'\n";
        }
        if (!remark.equals("-")) {
            filter += "and remark like '" + remark + "%'\n";
        }
        if (!stockCode.equals("-")) {
            filter += "and stock_code ='" + stockCode + "'\n";
        }
        if (!pt.equals("-")) {
            filter += "and pt_code ='" + pt + "'\n";
        }
        if (!locCode.equals("-")) {
            filter += "and loc_code ='" + locCode + "'\n";
        }

        String sql = "select a.*,s.user_code,s.stock_name,l.loc_name,v.description\n" +
                "from (\n" +
                "select *\n" +
                "from process_his\n" +
                "" + filter + "\n" +
                ")a\n" +
                "join stock s on a.stock_code = s.stock_code\n" +
                "and a.comp_code = s.comp_code\n" +
                "and a.dept_id = s.dept_id\n" +
                "join location l on a.loc_code = l.loc_code\n" +
                "and a.comp_code = l.comp_code\n" +
                "and a.dept_id = l.dept_id\n" +
                "join vou_status v on a.pt_code = v.code\n" +
                "and a.comp_code = v.comp_code\n" +
                "and a.dept_id = v.dept_id";
        ResultSet rs = getResultSet(sql);
        if (rs != null) {
            try {
                //vou_no, stock_code, comp_code, dept_id, loc_code, vou_date, end_date, qty,
                // unit, avg_qty, price, remark, process_no,
                // pt_code, finished, deleted, created_by, updated_by, mac_id, user_code, stock_name, loc_name, description
                while (rs.next()) {
                    ProcessHis p = new ProcessHis();
                    ProcessHisKey key = new ProcessHisKey();
                    key.setCompCode(compCode);
                    key.setDeptId(deptId);
                    key.setVouNo(rs.getString("vou_no"));
                    key.setStockCode(rs.getString("stock_code"));
                    p.setKey(key);
                    p.setLocCode(rs.getString("loc_code"));
                    p.setVouDate(rs.getDate("vou_date"));
                    p.setEndDate(rs.getDate("end_date"));
                    p.setQty(rs.getFloat("qty"));
                    p.setUnit(rs.getString("unit"));
                    p.setPrice(rs.getFloat("price"));
                    p.setFinished(rs.getBoolean("finished"));
                    p.setRemark(rs.getString("remark"));
                    p.setProcessNo(rs.getString("process_no"));
                    p.setPtCode(rs.getString("pt_code"));
                    p.setDeleted(rs.getBoolean("deleted"));
                    p.setStockUsrCode(rs.getString("user_code"));
                    p.setStockName(rs.getString("stock_name"));
                    p.setPtName(rs.getString("description"));
                    p.setLocName(rs.getString("loc_name"));
                    p.setCreatedBy(rs.getString("created_by"));
                    p.setUpdatedBy(rs.getString("updated_by"));
                    list.add(p);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return list;
    }

    @Override
    public void delete(ProcessHisKey key) {
        String sql = "update process_his set deleted =1 where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        execSQL(sql);
    }

    @Override
    public void restore(ProcessHisKey key) {
        String sql = "update process_his set deleted =0 where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        execSQL(sql);
    }
}
