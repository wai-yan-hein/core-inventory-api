package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.WeightHis;
import cv.api.entity.WeightHisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class WeightDaoImpl extends AbstractDao<WeightHisKey, WeightHis> implements WeightDao {
    @Override
    public WeightHis save(WeightHis obj) {
        obj.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(obj, obj.getKey());
        return obj;
    }

    @Override
    public WeightHis findById(WeightHisKey key) {
        return getByKey(key);
    }

    @Override
    public boolean delete(WeightHisKey key) {
        WeightHis his = findById(key);
        if (his != null) {
            his.setDeleted(true);
            his.setUpdatedDate(LocalDateTime.now());
            update(his);
        }
        return true;
    }

    @Override
    public boolean restore(WeightHisKey key) {
        WeightHis his = findById(key);
        if (his != null) {
            his.setDeleted(false);
            his.setUpdatedDate(LocalDateTime.now());
            update(his);
        }
        return true;
    }

    @Override
    public List<WeightHis> getWeightHistory(String fromDate, String toDate, String traderCode,
                                            String stockCode, String vouNo, String remark,
                                            boolean deleted, String compCode, String tranSource, boolean draft) {
        remark = remark + "%";
        List<WeightHis> list = new ArrayList<>();
        String sql = """
                select a.*,s.user_code s_user_code,s.stock_name,t.user_code t_user_code,t.trader_name
                from (
                select *
                from weight_his
                where comp_code =?
                and deleted = ?
                and draft =?
                and date(vou_date) between ? and ?
                and (trader_code=? or '-'=?)
                and (stock_code = ? or '-' = ?)
                and (vou_no = ? or '-' = ?)
                and (remark = ? or '-%' = ?)
                and (tran_source = ? or '-' = ?)
                )a
                join trader t on a.trader_code = t.code
                and a.comp_code = t.comp_code
                join stock s on a.stock_code = s.stock_code
                and a.comp_code = s.comp_code
                order by a.vou_date desc
                """;
        try {
            ResultSet rs = getResult(sql, compCode, deleted, draft,
                    fromDate,toDate,
                    traderCode, traderCode,
                    stockCode, stockCode,
                    vouNo, vouNo, remark, remark, tranSource, tranSource);
            while (rs.next()) {
                WeightHis h = new WeightHis();
                WeightHisKey key = new WeightHisKey();
                key.setCompCode(rs.getString("comp_code"));
                key.setVouNo(rs.getString("vou_no"));
                h.setKey(key);
                h.setVouDate(rs.getTimestamp("vou_date").toLocalDateTime());
                h.setVouDateTime(Util1.toZonedDateTime(rs.getTimestamp("vou_date").toLocalDateTime()));
                h.setStockCode(rs.getString("stock_code"));
                h.setStockUserCode(rs.getString("s_user_code"));
                h.setStockName(rs.getString("stock_name"));
                h.setTraderCode(rs.getString("trader_code"));
                h.setTraderUserCode(rs.getString("t_user_code"));
                h.setTraderName(rs.getString("trader_name"));
                h.setWeight(rs.getDouble("weight"));
                h.setTotalWeight(rs.getDouble("total_weight"));
                h.setTotalQty(rs.getDouble("total_qty"));
                h.setTotalBag(rs.getDouble("total_bag"));
                h.setCreatedBy(rs.getString("created_by"));
                h.setDeleted(rs.getBoolean("deleted"));
                h.setTranSource(rs.getString("tran_source"));
                h.setDescription(rs.getString("description"));
                h.setRemark(rs.getString("remark"));
                h.setPost(rs.getBoolean("post"));
                h.setDraft(rs.getBoolean("draft"));
                list.add(h);
            }
        } catch (Exception e) {
            log.error("getWeightHistory : " + e.getMessage());
        }
        return list;
    }


}
