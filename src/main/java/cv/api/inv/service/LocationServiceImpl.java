/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.dao.LocationDao;
import cv.api.inv.entity.Location;
import cv.api.inv.entity.LocationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationDao dao;

    @Autowired
    private SeqTableService seqService;

    @Override
    public Location save(Location loc) throws Exception {
        if (loc.getKey().getLocCode() == null) {
            Integer macId = loc.getMacId();
            String compCode = loc.getKey().getCompCode();
            String locCode = getLocationCode(macId, compCode);
            loc.getKey().setLocCode(locCode);
        }
        return dao.save(loc);
    }

    @Override
    public List<Location> findAll(String compCode, Integer deptId) {
        return dao.findAll(compCode, deptId);
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    @Override
    public List<Location> search(String parent) {
        return dao.search(parent);
    }

    @Override
    public List<Location> unUpload() {
        return dao.unUpload();
    }

    private String getLocationCode(Integer macId, String compCode) {

        int seqNo = seqService.getSequence(macId, "Location", "-", compCode);

        return String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 4 + "d", seqNo);
    }

    @Override
    public Location findByCode(LocationKey code) {
        return dao.findByCode(code);
    }
}
