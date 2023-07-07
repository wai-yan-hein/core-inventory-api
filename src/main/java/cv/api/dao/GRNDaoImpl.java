package cv.api.dao;

import cv.api.entity.GRN;
import cv.api.entity.GRNKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class GRNDaoImpl extends AbstractDao<GRNKey, GRN> implements GRNDao {
    @Override
    public GRN findByCode(GRNKey key) {
        return getByKey(key);
    }

    @Override
    public GRN save(GRN b) {
        saveOrUpdate(b, b.getKey());
        return b;
    }

    @Override
    public List<GRN> findAll(String compCode, Integer deptId) {
        String hsql = "select o from GRN o where o.key.compCode ='" + compCode + "' and o.key.deptId =" + deptId + "";
        return findHSQL(hsql);
    }

    @Override
    public boolean delete(GRNKey key) {
        String sql = "update grn set deleted = true where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        execSql(sql);
        return true;
    }

    @Override
    public boolean restore(GRNKey key) {
        String sql = "update grn set deleted = false where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        execSql(sql);
        return true;
    }

    @Override
    public List<GRN> search(String batchNo, String compCode, Integer deptId) {
        List<GRN> list = new ArrayList<>();
        String sql = """
                select a.batch_no,t.trader_name,a.vou_no
                from (
                select batch_no,trader_code,comp_code,dept_id,vou_no
                from grn
                where comp_code=?
                and (dept_id =? or 0=?)
                and deleted = false
                and closed =0
                and batch_no like ?
                order by batch_no
                limit 20
                )a
                join trader t on
                a.trader_code = t.code
                and a.comp_code = t.comp_code
                and a.dept_id = t.dept_id""";
        try {
            ResultSet rs = getResult(sql, compCode, deptId, deptId, batchNo + "%");
            if (rs != null) {
                while (rs.next()) {
                    GRN g = new GRN();
                    GRNKey key = new GRNKey();
                    key.setVouNo(rs.getString("vou_no"));
                    g.setKey(key);
                    g.setBatchNo(rs.getString("batch_no"));
                    g.setTraderName(rs.getString("trader_name"));
                    list.add(g);
                }
            }
        } catch (Exception e) {
            log.error("search : " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean open(GRNKey key) {
        String sql = "update grn set closed =0 where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId();
        execSql(sql);
        return true;
    }

    @Override
    public GRN findByBatchNo(String batchNo, String compCode, Integer deptId) {
        GRN grn = new GRN();
        String sql = """
                select batch_no,trader_code,comp_code,dept_id,vou_no
                from grn
                where comp_code=?
                and (dept_id =? or 0=?)
                and deleted = false
                and batch_no = ?
                limit 1
                """;
        ResultSet rs = getResult(sql, compCode, deptId, deptId, batchNo);
        try {
            if (rs.next()) {
                GRNKey key = new GRNKey();
                key.setVouNo(rs.getString("vou_no"));
                key.setCompCode(rs.getString("comp_code"));
                key.setDeptId(rs.getInt("dept_id"));
                grn.setKey(key);
                grn.setBatchNo(rs.getString("batch_no"));
                grn.setTraderCode(rs.getString("trader_code"));
                return grn;
            }
        } catch (Exception e) {
            log.error("findByBatchNo : " + e.getMessage());
        }
        return grn;
    }

}
