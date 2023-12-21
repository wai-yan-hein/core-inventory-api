package cv.api.dao;

import cv.api.entity.VouDiscount;
import cv.api.entity.VouDiscountKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class VouDiscountDaoImpl extends AbstractDao<VouDiscountKey, VouDiscount> implements VouDiscountDao {
    @Override
    public VouDiscount save(VouDiscount p) {
        saveOrUpdate(p, p.getKey());
        return p;
    }

    @Override
    public List<VouDiscount> getVoucherDiscount(String vouNo, String compCode) {
        String sql = """
                select v.*,u.unit_name
                from vou_discount v join stock_unit u
                on v.unit = u.unit_code
                and v.comp_code = u.comp_code
                where v.vou_no =?
                and v.comp_code =?
                order by unique_id""";
        ResultSet rs = getResult(sql, vouNo, compCode);
        List<VouDiscount> list = new ArrayList<>();
        if (rs != null) {
            try {
                while (rs.next()) {
                    VouDiscount e = new VouDiscount();
                    VouDiscountKey key = new VouDiscountKey();
                    key.setCompCode(rs.getString("comp_code"));
                    key.setUniqueId(rs.getInt("unique_id"));
                    key.setVouNo(rs.getString("vou_no"));
                    e.setKey(key);
                    e.setDescription(rs.getString("description"));
                    e.setUnit(rs.getString("unit"));
                    e.setUnitName(rs.getString("unit_name"));
                    e.setQty(rs.getDouble("qty"));
                    e.setPrice(rs.getDouble("price"));
                    e.setAmount(rs.getDouble("amount"));
                    list.add(e);
                }
            } catch (Exception e) {
                log.error("search : " + e.getMessage());
            }
        }
        return list;
    }

    @Override
    public void delete(VouDiscountKey key) {
        remove(key);
    }

    @Override
    public List<VouDiscount> getDescription(String str, String compCode) {
        str += "%";
        String sql = """
                select description
                from vou_discount
                where description like ?
                and comp_code= ?
                """;
        List<VouDiscount> list = new ArrayList<>();
        ResultSet rs = getResult(sql, str, compCode);
        try {
            while (rs.next()) {
                VouDiscount d = new VouDiscount();
                d.setDescription(rs.getString("description"));
                list.add(d);
            }
        } catch (Exception e) {
            log.error("getDescription : " + e.getMessage());
        }
        return list;
    }
}
