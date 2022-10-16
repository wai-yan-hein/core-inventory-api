package cv.api.inv.dao;

import cv.api.inv.entity.THDetailKey;
import cv.api.inv.entity.TransferHisDetail;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransferHisDetailDaoImpl extends AbstractDao<String, TransferHisDetail> implements TransferHisDetailDao {
    @Override
    public TransferHisDetail save(TransferHisDetail th) {
        persist(th);
        return th;
    }

    @Override
    public int delete(String code) {
        String delSql = "delete from transfer_his_detail  where td_code = '" + code + "'";
        execSQL(delSql);
        return 1;
    }

    @Override
    public List<TransferHisDetail> search(String vouNo, String compCode, Integer deptId) {
        List<TransferHisDetail> list = new ArrayList<>();
        String sql = "select td.*,s.stock_name,rel.rel_name\n" +
                "from transfer_his_detail td \n" +
                "join stock s on td.stock_code = s.stock_code\n" +
                "join unit_relation rel on s.rel_code = rel.rel_code\n" +
                "where td.vou_no ='" + vouNo + "'\n" +
                "and td.comp_code ='" + compCode + "'\n" +
                "and td.dept_id = " + deptId + "\n" +
                "order by td.unique_id";
        ResultSet rs = getResultSet(sql);
        if (rs != null) {
            try {
                while (rs.next()) {
                    //td_code, vou_no, stock_code, qty, unit, unique_id, comp_code, dept_id, stock_name, rel_name
                    TransferHisDetail td = new TransferHisDetail();
                    THDetailKey key = new THDetailKey();
                    key.setDeptId(rs.getInt("dept_id"));
                    key.setVouNo(rs.getString("vou_no"));
                    key.setTdCode(rs.getString("td_code"));
                    td.setKey(key);
                    td.setStockCode(rs.getString("stock_code"));
                    td.setStockName(rs.getString("stock_name"));
                    td.setCompCode(rs.getString("comp_code"));
                    td.setQty(rs.getFloat("qty"));
                    td.setUnitCode(rs.getString("unit"));
                    td.setRelName(rs.getString("rel_name"));
                    td.setUniqueId(rs.getInt("unique_id"));
                    list.add(td);
                }
            } catch (Exception e) {

            }
        }
        return list;
    }
}