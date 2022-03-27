package cv.api.inv.service;

import cv.api.inv.entity.SysProperty;

import java.util.List;

public interface SysPropertyService {
    SysProperty save(SysProperty property) throws  Exception;

    List<SysProperty> search(String compCode);
}
