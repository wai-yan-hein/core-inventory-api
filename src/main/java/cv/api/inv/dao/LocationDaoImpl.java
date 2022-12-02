/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.common.Util1;
import cv.api.inv.entity.Location;
import cv.api.inv.entity.LocationKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
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
        persist(ch);
        return ch;
    }

    @Override
    public List<Location> findAll(String compCode, Integer deptId) {
        String hsql = "select o from Location o where o.key.compCode ='" + compCode + "' and (o.key.deptId = " + deptId + " or 0 = " + deptId + ")";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from Location o where o.locationCode='" + id + "'";
        return execUpdateOrDelete(hsql);
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
        ResultSet rs = getResultSet(sql);
        try {
            if (rs.next()) {
                Date date = rs.getTimestamp("date");
                if (date != null) {
                    return date;
                }            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Util1.getOldDate();
    }

    @Override
    public List<Location> getLocation(String updatedDate) {
        String hsql = "select o from Location o where o.updatedDate > '" + updatedDate + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<LocationKey> getLocation(Integer deptId) {
        List<LocationKey> keys = new ArrayList<>();
        String hsql = "select o from Location o where o.key.deptId =" + deptId + "";
        List<Location> list = findHSQL(hsql);
        list.forEach(l -> {
            keys.add(l.getKey());
        });
        return keys;
    }

    @Override
    public Location findByCode(LocationKey code) {
        return getByKey(code);
    }

}
