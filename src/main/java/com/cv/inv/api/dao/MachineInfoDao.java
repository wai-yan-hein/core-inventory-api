/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.MachineInfo;
import java.util.List;

/**
 *
 * @author lenovo
 */
 public interface MachineInfoDao {

     MachineInfo save(MachineInfo machineInfo) throws Exception;

     int getMax(String machineName) throws Exception;

     List<MachineInfo> findAll() throws Exception;

     MachineInfo findById(String id) throws Exception;

     List<MachineInfo> search(String name, String ip);

     List<MachineInfo> searchM(String name);
}
