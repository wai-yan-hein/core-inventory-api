package cv.api.dao;

import cv.api.entity.VouDiscount;
import cv.api.entity.VouDiscountKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VouDiscountDaoImpl extends AbstractDao<VouDiscountKey, VouDiscount> implements VouDiscountDao {
    private final DatabaseClient client;

    @Override
    public VouDiscount save(VouDiscount p) {
        saveOrUpdate(p, p.getKey());
        return p;
    }

    public Flux<VouDiscount> getVoucherDiscount(String vouNo, String compCode) {
        String sql = """
                select v.*,u.unit_name
                from vou_discount v join stock_unit u
                on v.unit = u.unit_code
                and v.comp_code = u.comp_code
                where v.vou_no =:vouNo
                and v.comp_code =:compCode
                order by unique_id""";
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> {
                    VouDiscount e = new VouDiscount();
                    VouDiscountKey key = new VouDiscountKey();
                    key.setCompCode(row.get("comp_code", String.class));
                    key.setUniqueId(row.get("unique_id", Integer.class));
                    key.setVouNo(row.get("vou_no", String.class));
                    e.setKey(key);
                    e.setDescription(row.get("description", String.class));
                    e.setUnit(row.get("unit", String.class));
                    e.setUnitName(row.get("unit_name", String.class));
                    e.setQty(row.get("qty", Double.class));
                    e.setPrice(row.get("price", Double.class));
                    e.setAmount(row.get("amount", Double.class));
                    return e;
                }).all();
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
