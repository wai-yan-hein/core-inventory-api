/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.Location;
import cv.api.entity.LocationKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Repository
@Slf4j
public class LocationDaoImpl extends AbstractDao<LocationKey, Location> implements LocationDao {


    @Override
    public Location save(Location ch) {
        saveOrUpdate(ch, ch.getKey());
        return ch;
    }

    @Override
    public List<Location> findAll(String compCode, Integer deptId) {
        String hsql = "select o from Location o where o.key.compCode ='" + compCode + "' and (o.key.deptId = " + deptId + " or 0 = " + deptId + ")";
        return findHSQL(hsql);
    }

    @Override
    public List<Location> findAll() {
        return findHSQL("select o from Location o");
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from location where loc_code ='" + id + "'";
        execSql(hsql);
        return 1;
    }

    @Override
    public List<Location> search(String parent) {
        String hsql = "select o from Location o where o.parentCode ='" + parent + "'";
        return findHSQL(hsql);

    }

    @Override
    public List<Location> unUpload() {
        String hsql = "select o from Location o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from location";
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

    @Override
    public List<Location> getLocation(LocalDateTime updatedDate) {
        String hsql = "select o from Location o where o.updatedDate > :updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }

    @Override
    public List<String> getLocation(Integer deptId) {
        List<String> location = new ArrayList<>();
        String hsql = "select o from Location o where o.mapDeptId =" + deptId + "";
        List<Location> list = findHSQL(hsql);
        list.forEach(l -> {
            location.add(l.getKey().getLocCode());
        });
        return location;
    }

    @Override
    public Location findByCode(LocationKey code) {
        return getByKey(code);
    }

}
