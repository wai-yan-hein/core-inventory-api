/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.Region;
import cv.api.inv.entity.RegionKey;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author WSwe
 */
@Repository
public class RegionDaoImpl extends AbstractDao<RegionKey, Region> implements RegionDao {


    @Override
    public Region save(Region region) {
        persist(region);
        return region;
    }

    @Override
    public Region findByCode(RegionKey id) {
        return getByKey(id);
    }

    @Override
    public List<Region> search(String code, String name, String compCode, String parentCode) {
        String strSql = "select o from Region o ";
        String strFilter = "";

        if (!code.equals("-")) {
            strFilter = "o.regionCode = '" + code + "'";
        }

        if (!name.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.regionName like '%" + name + "%'";
            } else {
                strFilter = strFilter + " and o.regionName like '%" + name + "%'";
            }
        }

        if (!compCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.compCode = '" + compCode + "'";
            } else {
                strFilter = strFilter + " and o.compCode= '" + compCode + "'";
            }
        }
        if (!parentCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.parentRegion = '" + parentCode + "'";
            } else {
                strFilter = strFilter + " and o.parentRegion = '" + parentCode + "'";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by o.regionName";
        }

        return (List<Region>) findHSQL(strSql);
    }

    @Override
    public int delete(String code) {
        String strSql = "delete from Region o where o.regCode = '"
                + code + "'";
        return execUpdateOrDelete(strSql);
    }

    @Override
    public List<Region> findAll(String compCode) {
        String hsql = "select o from Region o where o.key.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }
}
