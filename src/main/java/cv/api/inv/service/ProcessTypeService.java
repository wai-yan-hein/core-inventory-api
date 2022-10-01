package cv.api.inv.service;

import cv.api.inv.entity.ProcessType;

import java.util.List;

public interface ProcessTypeService {
    ProcessType save(ProcessType type);

    List<ProcessType> getProcessType(String compCode);
}
