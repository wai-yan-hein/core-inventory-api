package cv.api.inv.service;

import cv.api.inv.entity.GRNDetail;
import cv.api.inv.entity.GRNDetailKey;

public interface GRNDetailService {

    GRNDetail save(GRNDetail b);

    void delete(GRNDetailKey key);
}
