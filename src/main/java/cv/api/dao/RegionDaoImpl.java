/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.Region;
import cv.api.entity.RegionKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author WSwe
 */
@Repository
@Slf4j
public class RegionDaoImpl extends AbstractDao<RegionKey, Region> implements RegionDao {


    @Override
    public Region save(Region region) {
        saveOrUpdate(region, region.getKey());
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

        return findHSQL(strSql);
    }

    @Override
    public int delete(RegionKey key) {
        Region r =getByKey(key);
        r.setDeleted(true);
        r.setUpdatedDate(LocalDateTime.now());
        update(r);
        return 1;
    }

    @Override
    public List<Region> findAll(String compCode) {
        String hsql = "select o from Region o where o.key.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<Region> getRegion(LocalDateTime updatedDate) {
        String hsql = "select o from Region o where o.updatedDate > :updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from region";
        ResultSet rs = getResult(sql);
        try {
            if (rs.next()) {
                Date date = rs.getTimestamp("date");
                if (date != null) {
                    return date;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Util1.getOldDate();
    }
}
