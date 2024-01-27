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
        ch.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(ch, ch.getKey());
        return ch;
    }

    @Override
    public List<Location> findAll(String compCode, Integer deptId) {
        List<Location> list = new ArrayList<>();

        String sql= """
                select l.*,w.description
                from location l left join warehouse w
                on l.warehouse_code = w.code
                and l.comp_code = w.comp_code
                where l.deleted = false
                and l.active = true
                and l.comp_code =?
                """;
        try {
            ResultSet rs = getResult(sql,compCode);
            while (rs.next()){
                Location l = new Location();
                LocationKey key = new LocationKey();
                key.setCompCode(rs.getString("comp_code"));
                key.setLocCode(rs.getString("loc_code"));
                l.setKey(key);
                l.setDeptId(rs.getInt("dept_id"));
                l.setMacId(rs.getInt("mac_id"));
                l.setLocName(rs.getString("loc_name"));
                l.setCalcStock(rs.getBoolean("calc_stock"));
                l.setCreatedBy(rs.getString("created_by"));
                l.setUpdatedBy(rs.getString("updated_by"));
                l.setUserCode(rs.getString("user_code"));
                l.setDeptCode(rs.getString("dept_code"));
                l.setCashAcc(rs.getString("cash_acc"));
                l.setDeleted(rs.getBoolean("deleted"));
                l.setActive(rs.getBoolean("active"));
                l.setWareHouseCode(rs.getString("warehouse_code"));
                l.setWareHouseName(rs.getString("description"));
                list.add(l);
            }
        }catch (Exception e){
            log.error("findAll : "+e.getMessage());
        }
        //loc_code, comp_code, dept_id, mac_id, loc_name, parent, calc_stock,
        // updated_date, location_type, created_date, created_by, updated_by,
        // user_code, intg_upd_status, map_dept_id, dept_code, cash_acc, deleted, active, warehouse_code
       return list;
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
