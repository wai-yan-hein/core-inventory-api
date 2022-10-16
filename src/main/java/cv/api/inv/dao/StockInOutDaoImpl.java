/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.StockIOKey;
import cv.api.inv.entity.StockInOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Slf4j
@Repository
public class StockInOutDaoImpl extends AbstractDao<StockIOKey, StockInOut> implements StockInOutDao {
    @Autowired
    private StockInOutDetailDao dao;

    @Override
    public StockInOut findById(StockIOKey id) {
        return getByKey(id);
    }

    @Override
    public StockInOut save(StockInOut stock) {
        persist(stock);
        return stock;
    }

    @Override
    public List<StockInOut> search(String fromDate, String toDate, String remark, String desp,
                                   String vouNo, String userCode, String vouStatus) {
        String strFilter = "";
        if (!fromDate.equals("-") && !toDate.equals("-")) {
            strFilter = "date(o.vouDate) between '" + fromDate
                    + "' and '" + toDate + "'";
        } else if (!fromDate.endsWith("-")) {
            strFilter = "date(o.vouDate) >= '" + fromDate + "'";
        } else if (!toDate.equals("-")) {
            strFilter = "date(o.vouDate) <= '" + toDate + "'";
        }
        if (!vouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.vouNo = '" + vouNo + "'";
            } else {
                strFilter = strFilter + " and o.vouNo = '" + vouNo + "'";
            }
        }
        if (!desp.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.description like '" + desp + "%'";
            } else {
                strFilter = strFilter + " and o.description like '" + desp + "%'";
            }
        }
        if (!remark.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.remark like '" + remark + "%'";
            } else {
                strFilter = strFilter + " and o.remark like '" + remark + "%'";
            }
        }
        if (!userCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.createdBy = '" + userCode + "'";
            } else {
                strFilter = strFilter + " and o.createdBy = '" + userCode + "'";
            }
        }
        if (!vouStatus.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.vouStatus.code = '" + vouStatus + "'";
            } else {
                strFilter = strFilter + " and o.vouStatus.code = '" + vouStatus + "'";
            }
        }
        String strSql = "select o from StockInOut o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by o.vouDate,o.vouNo desc";
        }
        return findHSQL(strSql);

    }

    @Override
    public void delete(StockIOKey key) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        Integer deptId = key.getDeptId();
        String sql = "update stock_in_out set deleted =1 where vou_no ='" + vouNo + "' and comp_code='" + compCode + "' and dept_id =" + deptId + "";
        execSQL(sql);

    }

    @Override
    public List<StockInOut> unUpload() {
        String hsql = "select o from StockInOut o where intgUpdStatus is null";
        List<StockInOut> list = findHSQL(hsql);
        list.forEach((o) -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer depId = o.getKey().getDeptId();
            o.setListSH(dao.search(vouNo, compCode, depId));
        });
        return list;
    }

}
