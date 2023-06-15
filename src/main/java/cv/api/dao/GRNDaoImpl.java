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
        saveOrUpdate(b,b.getKey());
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
    public List<GRN> search(String batchNo, String compCode, Integer deptId) {
        List<GRN> list = new ArrayList<>();
        String sql = "select a.batch_no,t.trader_name,a.vou_no\n" +
                "from (\n" +
                "select batch_no,trader_code,comp_code,dept_id,vou_no\n" +
                "from grn\n" +
                "where comp_code='" + compCode + "'\n" +
                "and (dept_id =" + deptId + " or 0=" + deptId + ")\n" +
                "and deleted = false\n" +
                "and closed =0\n" +
                "and batch_no like '" + batchNo + "%'\n" +
                "order by batch_no\n" +
                "limit 20\n" +
                ")a\n" +
                "join trader t on\n" +
                "a.trader_code = t.code\n" +
                "and a.comp_code = t.comp_code\n" +
                "and a.dept_id = t.dept_id";
        try {
            ResultSet rs = getResult(sql);
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
        return true;    }

}
