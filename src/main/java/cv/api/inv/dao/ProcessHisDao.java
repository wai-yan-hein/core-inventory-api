package cv.api.inv.dao;

import cv.api.inv.entity.ProcessHis;
import cv.api.inv.entity.ProcessHisKey;

public interface ProcessHisDao {
    ProcessHis save(ProcessHis ph);

    ProcessHis findById(ProcessHisKey key);
}
