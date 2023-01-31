package cv.api.inv.dao;

import cv.api.inv.entity.GRNDetail;
import cv.api.inv.entity.GRNDetailKey;
import org.springframework.stereotype.Repository;

@Repository
public class GRNDetailDaoImpl extends AbstractDao<GRNDetailKey, GRNDetail> implements GRNDetailDao {
    @Override
    public GRNDetail save(GRNDetail b) {
        persist(b);
        return b;
    }

    @Override
    public void delete(GRNDetailKey key) {
        String sql = "delete from grn_detail where vou_no='" + key.getVouNo() + "' and '" + key.getCompCode() + "' and '" + key.getDeptId() + "' and '" + key.getUniqueId() + "'";
        execSQL(sql);
    }
}
