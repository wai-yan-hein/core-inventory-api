/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.MachineInfo;
import java.util.List;

/**
 *
 * @author lenovo
 */
public interface MachineInfoService {

    public MachineInfo save(MachineInfo machineInfo) throws Exception;

    public int getMax(String machineName) throws Exception;

    public List<MachineInfo> findAll() throws Exception;

    public MachineInfo findById(String id) throws Exception;

    public List<MachineInfo> search(String name, String ip);

    public List<MachineInfo> searchM(String name);

}
