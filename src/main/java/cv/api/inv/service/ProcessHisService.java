package cv.api.inv.service;

import cv.api.inv.entity.ProcessHis;
import cv.api.inv.entity.ProcessHisKey;

public interface ProcessHisService {
    ProcessHis save(ProcessHis ph);

    ProcessHis findById(ProcessHisKey key);
}
