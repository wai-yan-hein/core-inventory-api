package cv.api.inv.dao;

import cv.api.inv.entity.TmpStockIO;
import cv.api.inv.entity.TmpStockIOKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class TmpDaoImpl extends AbstractDao<TmpStockIOKey, TmpStockIO> implements TmpDao {
    @Override
    public TmpStockIO save(TmpStockIO io) {
        persist(io);
        return io;
    }

    @Override
    public List<TmpStockIO> getStockIO(String stockCode, String compCode, Integer deptId, Integer macId) {
        List<TmpStockIO> list = new ArrayList<>();
        String sql = "select tran_option, tran_date, stock_code, loc_code, op_qty, pur_qty, in_qty, sale_qty, out_qty, mac_id, remark, vou_no, comp_code, dept_id\n"
                + "from tmp_stock_io_column"
                + " where mac_id =" + macId + "\n"
                + " and comp_code ='" + compCode + "'\n"
                + " and dept_id =" + deptId + "\n"
                + " order by tran_date,tran_option,vou_no";
        ResultSet rs = getResultSet(sql);
        if (rs != null) {
            try {
                while (rs.next()) {
                    TmpStockIO io = new TmpStockIO();
                    TmpStockIOKey key = new TmpStockIOKey();
                    key.setCompCode(compCode);
                    key.setDeptId(deptId);
                    key.setStockCode(stockCode);
                    key.setMacId(macId);
                    key.setLocCode(rs.getString("loc_code"));
                    key.setTranDate(rs.getDate("tran_date"));
                    key.setTranOption(rs.getString("tran_option"));
                    key.setVouNo(rs.getString("vou_no"));
                    io.setKey(key);
                    io.setOpQty(rs.getFloat("op_qty"));
                    io.setInQty(rs.getFloat("in_qty"));
                    io.setOutQty(rs.getFloat("out_qty"));
                    io.setPurQty(rs.getFloat("pur_qty"));
                    io.setSaleQty(rs.getFloat("sale_qty"));
                    list.add(io);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return list;
    }
}
