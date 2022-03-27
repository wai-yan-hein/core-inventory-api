package cv.api.inv.dao;

import cv.api.inv.entity.SysProperty;

import java.util.List;

public interface SysPropertyDao {
    SysProperty save(SysProperty property) throws Exception;

    List<SysProperty> search(String compCode);
}
