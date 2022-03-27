/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.dao.MachineInfoDao;
import cv.api.inv.entity.MachineInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class MachineInfoServiceImpl implements MachineInfoService {

    @Autowired
    private MachineInfoDao machineInfoDao;

    @Override
    public MachineInfo save(MachineInfo machineInfo) throws Exception {
        return machineInfoDao.save(machineInfo);
    }

    @Override
    public int getMax(String machineName) throws Exception {
        return machineInfoDao.getMax(machineName);
    }

    @Override
    public List<MachineInfo> findAll() throws Exception {
        return machineInfoDao.findAll();
    }

    @Override
    public MachineInfo findById(String id) throws Exception {
        return machineInfoDao.findById(id);
    }

    @Override
    public List<MachineInfo> search(String name, String ip) {
        return machineInfoDao.search(name, ip);
    }

    @Override
    public List<MachineInfo> searchM(String name) {
        return machineInfoDao.searchM(name);
    }
}
