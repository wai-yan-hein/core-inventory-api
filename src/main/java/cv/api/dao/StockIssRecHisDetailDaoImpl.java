package cv.api.dao;

import cv.api.entity.StockIssRecDetail;
import cv.api.entity.StockIssRecDetailKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class StockIssRecHisDetailDaoImpl extends AbstractDao<StockIssRecDetailKey, StockIssRecDetail> implements StockIssRecDetailDao {
    @Override
    public StockIssRecDetail save(StockIssRecDetail obj) {
        saveOrUpdate(obj, obj.getKey());
        return obj;
    }

    @Override
    public boolean delete(StockIssRecDetailKey key) {
        remove(key);
        return true;
    }

    @Override
    public boolean deleteStockIssRecDetail(String vouNo, String compCode) {
        String sql = "delete from iss_rec_his_detail where vou_no =? and comp_code =?";
        deleteRecords(sql, vouNo, compCode);
        return true;
    }
    @Override
    public List<StockIssRecDetail> getStockIssRecDetail(String vouNo, String compCode) {
        List<StockIssRecDetail> list = new ArrayList<>();
        String sql = """
                select op.*,s.stock_name,s.user_code 
                from iss_rec_his_detail op
                join location l on op.loc_code = l.loc_code
                and op.comp_code =l.comp_code
                join stock s on op.stock_code = s.stock_code
                and op.comp_code =s.comp_code
                where op.vou_no =?
                and op.comp_code =?
                """;
        ResultSet rs = getResult(sql, vouNo, compCode);
        try {
            while (rs.next()) {
                StockIssRecDetail d = new StockIssRecDetail();
                StockIssRecDetailKey key = new StockIssRecDetailKey();
                key.setCompCode(rs.getString("comp_code"));
                key.setVouNo(rs.getString("vou_no"));
                key.setUniqueId(rs.getInt("unique_id"));
                d.setDeptId(rs.getInt("dept_id"));
                d.setStockCode(rs.getString("stock_code"));
                d.setUserCode(rs.getString("user_code"));
                d.setStockName(rs.getString("stock_name"));
                d.setLocCode(rs.getString("loc_code"));
                d.setWet(rs.getDouble("wet"));
                d.setBag(rs.getDouble("bag"));
                d.setQty(rs.getDouble("qty"));
                d.setWeight(rs.getDouble("weight"));
                d.setRice(rs.getDouble("rice"));
                d.setPrice(rs.getDouble("price"));
                d.setAmount(rs.getDouble("amount"));
                d.setKey(key);
                list.add(d);
            }
        } catch (Exception e) {
            log.error("getStockIssRecDetail : " + e.getMessage());
        }
        return list;
    }


}
