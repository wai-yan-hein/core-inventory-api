package cv.api.inv.dao;

import cv.api.inv.entity.OPHis;
import cv.api.inv.entity.OPHisKey;

import java.util.List;

public interface OPHisDao {
    OPHis save(OPHis op);

    List<OPHis> search(String compCode);

    OPHis findByCode(OPHisKey key);
    List<OPHis> unUpload();
    void delete(OPHisKey key);


}
