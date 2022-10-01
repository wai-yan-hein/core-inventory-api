package cv.api.inv.dao;

import cv.api.inv.entity.ProcessType;

import java.util.List;

public interface ProcessTypeDao {
    ProcessType save(ProcessType type);

    List<ProcessType> getProcessType(String compCode);
}
