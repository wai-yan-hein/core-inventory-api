package cv.api.dao;

import cv.api.entity.THDetailKey;
import cv.api.entity.TransferHisDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class TransferHisDetailDaoImpl extends AbstractDao<THDetailKey, TransferHisDetail> implements TransferHisDetailDao {
    @Override
    public TransferHisDetail save(TransferHisDetail th) {
        saveOrUpdate(th, th.getKey());
        return th;
    }

    @Override
    public int delete(THDetailKey key) {
        remove(key);
        return 1;
    }

    @Override
    public List<TransferHisDetail> search(String vouNo, String compCode, Integer deptId) {
        List<TransferHisDetail> list = new ArrayList<>();
        String sql = """
                select td.*,s.user_code,s.stock_name,st.stock_type_name,rel.rel_name
                from transfer_his_detail td\s
                join stock s on td.stock_code = s.stock_code
                and td.comp_code = s.comp_code
                join stock_type st on s.stock_type_code = st.stock_type_code
                and s.comp_code = st.comp_code
                left join unit_relation rel on s.rel_code = rel.rel_code
                and td.comp_code = rel.comp_code
                where td.vou_no =?
                and td.comp_code =?
                order by td.unique_id""";
        ResultSet rs = getResult(sql,vouNo,compCode);
        if (rs != null) {
            try {
                while (rs.next()) {
                    //td_code, vou_no, stock_code, qty, unit, unique_id, comp_code, dept_id, stock_name, rel_name
                    TransferHisDetail td = new TransferHisDetail();
                    THDetailKey key = new THDetailKey();
                    key.setVouNo(rs.getString("vou_no"));
                    key.setUniqueId(rs.getInt("unique_id"));
                    key.setCompCode(rs.getString("comp_code"));
                    td.setKey(key);
                    td.setDeptId(rs.getInt("dept_id"));
                    td.setUserCode(rs.getString("user_code"));
                    td.setStockCode(rs.getString("stock_code"));
                    td.setStockName(rs.getString("stock_name"));
                    td.setGroupName(rs.getString("stock_type_name"));
                    td.setQty(rs.getFloat("qty"));
                    td.setUnitCode(rs.getString("unit"));
                    td.setRelName(rs.getString("rel_name"));
                    td.setWeight(rs.getFloat("weight"));
                    td.setWeightUnit(rs.getString("weight_unit"));
                    td.setTotalWeight(rs.getFloat("total_weight"));
                    list.add(td);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return list;
    }

    @Override
    public List<TransferHisDetail> searchDetail(String vouNo, String compCode, Integer deptId) {
        List<TransferHisDetail> list = new ArrayList<>();
        String sql = "select * from transfer_his_detail where vou_no ='" + vouNo + "' and comp_code ='" + compCode + "' and dept_id =" + deptId + " order by unique_id";
        ResultSet rs = getResult(sql);
        if (rs != null) {
            try {
                while (rs.next()) {
                    //td_code, vou_no, stock_code, qty, unit, unique_id, comp_code, dept_id
                    TransferHisDetail td = new TransferHisDetail();
                    THDetailKey key = new THDetailKey();
                    key.setVouNo(rs.getString("vou_no"));
                    key.setCompCode(rs.getString("comp_code"));
                    key.setUniqueId(rs.getInt("unique_id"));
                    td.setKey(key);
                    td.setDeptId(rs.getInt("dept_id"));
                    td.setStockCode(rs.getString("stock_code"));
                    td.setQty(rs.getFloat("qty"));
                    td.setUnitCode(rs.getString("unit"));
                    list.add(td);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return list;
    }
}
