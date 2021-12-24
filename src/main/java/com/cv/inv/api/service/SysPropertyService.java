package com.cv.inv.api.service;

import com.cv.inv.api.entity.SysProperty;

import java.util.List;

public interface SysPropertyService {
    SysProperty save(SysProperty property) throws  Exception;

    List<SysProperty> search(String compCode);
}
