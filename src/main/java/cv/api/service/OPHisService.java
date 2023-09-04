package cv.api.service;

import cv.api.entity.LocationKey;
import cv.api.entity.OPHis;
import cv.api.entity.OPHisKey;

import java.util.Date;
import java.util.List;

public interface OPHisService {
    OPHis save(OPHis op);

    OPHis findByCode(OPHisKey key);

    List<OPHis> search(String compCode);

    List<OPHis> unUpload();

    boolean delete(OPHisKey key);
    boolean restore(OPHisKey key);

    List<OPHis> search(String updatedDate, List<LocationKey> keys);

    Date getMaxDate();
}
