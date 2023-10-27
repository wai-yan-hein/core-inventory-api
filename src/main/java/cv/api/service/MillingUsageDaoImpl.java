package cv.api.service;

import cv.api.dao.AbstractDao;
import cv.api.entity.MillingUsage;
import cv.api.entity.MillingUsageKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class MillingUsageDaoImpl extends AbstractDao<MillingUsageKey, MillingUsage> implements MillingUsageDao {
    @Override
    public MillingUsage save(MillingUsage sdh) {
        saveOrUpdate(sdh, sdh.getKey());
        return sdh;
    }

    @Override
    public List<MillingUsage> getMillingUsage(String vouNo, String compCode) {
        String sql = """
                select u.*,s.user_code,s.stock_name
                from milling_usage u join stock s
                on u.stock_code = s.stock_code
                and u.comp_code = s.comp_code
                where u.vou_no=?
                and u.comp_code =?
                """;
        List<MillingUsage> list = new ArrayList<>();
        try {
            ResultSet rs = getResult(sql);
            while (rs.next()) {
                MillingUsage u = new MillingUsage();
                MillingUsageKey key = new MillingUsageKey();
                key.setVouNo(rs.getString("vou_no"));
                key.setCompCode(rs.getString("comp_code"));
                key.setUniqueId(rs.getInt("unique_id"));
                u.setKey(key);
                u.setQty(rs.getDouble("qty"));
                u.setUnit(rs.getString("unit"));
                u.setStockCode(rs.getString("stock_code"));
                u.setUserCode(rs.getString("user_code"));
                u.setStockName(rs.getString("stock_name"));
                list.add(u);
            }
        } catch (Exception e) {
            log.error("search : " + e.getMessage());
        }
        return list;
    }

    @Override
    public int delete(MillingUsageKey key) {
        return 0;
    }
}
