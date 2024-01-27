package cv.api.dao;

import cv.api.entity.LocationKey;
import cv.api.entity.OPHis;
import cv.api.entity.OPHisKey;

import java.util.Date;
import java.util.List;

public interface OPHisDao {
    OPHis save(OPHis op);

    List<OPHis> search(String compCode);

    OPHis findByCode(OPHisKey key);

    List<OPHis> unUpload();

    boolean delete(OPHisKey key);
    boolean restore(OPHisKey key);


    List<OPHis> search(String updatedDate, List<LocationKey> keys);



}
