package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.WeightHis;
import cv.api.entity.WeightHisDetail;
import cv.api.entity.WeightHisDetailKey;
import cv.api.model.WeightColumn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class WeightHisDetailDaoImpl extends AbstractDao<WeightHisDetailKey, WeightHisDetail> implements WeightDetailDao {
    @Override
    public WeightHisDetail save(WeightHisDetail obj) {
        saveOrUpdate(obj, obj.getKey());
        return obj;
    }

    @Override
    public boolean delete(WeightHisDetailKey key) {
        remove(key);
        return true;
    }

    @Override
    public boolean deleteWeightHisDetail(String vouNo, String compCode) {
        String sql = "delete from weight_his_detail where vou_no =? and comp_code =?";
        deleteRecords(sql, vouNo, compCode);
        return true;
    }
    @Override
    public List<WeightHisDetail> getWeightDetail(String vouNo, String compCode) {
        List<WeightHisDetail> list = new ArrayList<>();
        String sql = """
                select *
                from weight_his_detail
                where vou_no =?
                and comp_code =?
                """;
        ResultSet rs = getResult(sql, vouNo, compCode);
        try {
            while (rs.next()) {
                WeightHisDetail d = new WeightHisDetail();
                WeightHisDetailKey key = new WeightHisDetailKey();
                key.setCompCode(rs.getString("comp_code"));
                key.setVouNo(rs.getString("vou_no"));
                key.setUniqueId(rs.getInt("unique_id"));
                d.setWeight(rs.getDouble("weight"));
                d.setKey(key);
                list.add(d);
            }
        } catch (Exception e) {
            log.error("getWeightDetail : " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<WeightColumn> getWeightColumn(String vouNo, String compCode) {
        List<WeightColumn> listColumn = new ArrayList<>();
        List<WeightHisDetail> list = getWeightDetail(vouNo, compCode);

        if (!list.isEmpty()) {
            int totalElements = list.size();
            int fullRowCount = totalElements / 15;
            int remainingElements = totalElements % 15;

            for (int i = 0; i < fullRowCount + (remainingElements > 0 ? 1 : 0); i++) {
                Double[] rowData = new Double[15];

                for (int j = 0; j < 15; j++) {
                    int dataIndex = i * 15 + j;

                    if (dataIndex < totalElements) {
                        WeightHisDetail weightDetailHis = list.get(dataIndex);
                        double weight = weightDetailHis.getWeight();
                        rowData[j] = weight;
                    }
                }

                WeightColumn c = new WeightColumn();

                // Use reflection to dynamically set the fields w1, w2, ..., w15
                for (int k = 0; k < rowData.length; k++) {
                    try {
                        // Make the field accessible
                        Field field = WeightColumn.class.getDeclaredField("w" + (k + 1));
                        field.setAccessible(true);
                        field.set(c, Util1.getDouble(rowData[k]));
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                       log.error(e.getMessage());
                    }
                }
                double sum = Arrays.stream(rowData)
                        .filter(Objects::nonNull)
                        .mapToDouble(Double::doubleValue)
                        .sum();
                c.setTotal(sum);
                listColumn.add(c);
            }
        }
        return listColumn;
    }


}
