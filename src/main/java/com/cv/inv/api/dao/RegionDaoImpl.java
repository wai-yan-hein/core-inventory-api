/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.Region;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author WSwe
 */
@Repository
public class RegionDaoImpl extends AbstractDao<Integer, Region> implements RegionDao {

    @Override
    public Region save(Region region) {
        persist(region);
        return region;
    }

    @Override
    public Region findByCode(String id) {
        Region region = getByKey(Integer.parseInt(id));
        return region;
    }

    @Override
    public List<Region> search(String code, String name, String compCode, String parentCode) {
        String strSql = "select o from Region o ";
        String strFilter = "";

        if (!code.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.regionCode = '" + code + "'";
            } else {
                strFilter = strFilter + " and o.regionCode = '" + code + "'";
            }
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

        List<Region> listRegion = findHSQL(strSql);
        return listRegion;
    }

    @Override
    public int delete(String code) {
        String strSql = "delete from Region o where o.regCode = '"
                + code + "'";
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }

    @Override
    public List<Region> findAll(String compCode) {
        String hsql = "select o from Region o where o.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }
}
