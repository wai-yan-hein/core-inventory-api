package com.cv.inv.api.dao;

import com.cv.inv.api.entity.SysProperty;

import java.util.List;

public interface SysPropertyDao {
    SysProperty save(SysProperty property) throws Exception;

    List<SysProperty> search(String compCode);
}
