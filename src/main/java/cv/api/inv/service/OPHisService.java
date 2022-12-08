package cv.api.inv.service;

import cv.api.inv.entity.LocationKey;
import cv.api.inv.entity.OPHis;
import cv.api.inv.entity.OPHisKey;

import java.util.Date;
import java.util.List;

public interface OPHisService {
    OPHis save(OPHis op);

    OPHis findByCode(OPHisKey key);

    List<OPHis> search(String compCode);

    List<OPHis> unUpload();

    void delete(OPHisKey key);

    List<OPHis> search(String updatedDate, List<LocationKey> keys);

    Date getMaxDate();
}
