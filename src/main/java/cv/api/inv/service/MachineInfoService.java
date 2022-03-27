/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.MachineInfo;

import java.util.List;

/**
 * @author wai yan
 */
 public interface MachineInfoService {

     MachineInfo save(MachineInfo machineInfo) throws Exception;

     int getMax(String machineName) throws Exception;

     List<MachineInfo> findAll() throws Exception;

     MachineInfo findById(String id) throws Exception;

     List<MachineInfo> search(String name, String ip);

     List<MachineInfo> searchM(String name);

}
