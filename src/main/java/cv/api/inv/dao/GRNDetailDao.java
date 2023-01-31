package cv.api.inv.dao;

import cv.api.inv.entity.GRNDetail;
import cv.api.inv.entity.GRNDetailKey;

public interface GRNDetailDao {

    GRNDetail save(GRNDetail b);

    void delete(GRNDetailKey key);
}
