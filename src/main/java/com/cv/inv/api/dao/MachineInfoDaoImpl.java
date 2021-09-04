/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.MachineInfo;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lenovo
 */
@Repository
public class MachineInfoDaoImpl extends AbstractDao<Integer, MachineInfo> implements MachineInfoDao {


    @Override
    public MachineInfo save(MachineInfo machineInfo) throws Exception {
        persist(machineInfo);
        return machineInfo;
    }

    @Override
    public int getMax(String machineName) throws Exception {
        int maxId;
        Object obj;
        String strSQL = "select max(o.machineId) from MachineInfo o where o.machineName = '" + machineName + "'";
        obj = exeSQL(strSQL);
        if (obj == null) {
            maxId = 0;
        } else {
            maxId = Integer.parseInt(obj.toString());
        }

        return maxId;
    }

    @Override
    public List<MachineInfo> findAll() throws Exception {
        String hsql = "select o from MachineInfo o";
        return findHSQL(hsql);
    }

    @Override
    public MachineInfo findById(String id) throws Exception {
        return getByKey(Integer.parseInt(id));
    }
    
     @Override
    public List<MachineInfo> search(String name, String ip){
        String strSql = "";
        
        if(!name.equals("-")){
            if(strSql.isEmpty()){
                strSql = "o.machineName like '%" + name + "%'";
            }else{
                strSql = strSql + " and o.machineName like '%" + name + "%'";
            }
        }
        
        if(!ip.equals("-")){
            if(strSql.isEmpty()){
                strSql = "o.ipAddress like '%" + ip + "%'";
            }else{
                strSql = strSql + " and o.ipAddress like '%" + ip + "%'";
            }
        }
        
        if(strSql.isEmpty()){
            strSql = "select o from MachineInfo o";
        }else{
            strSql = "select o from MachineInfo o where " + strSql;
        }

         return (List<MachineInfo>) findHSQL(strSql);
    }
    
     @Override
    public List<MachineInfo> searchM(String name){
    
         String strSql = "";
        
        if(!name.equals("-")){
            if(strSql.isEmpty()){
                strSql = "o.machineName ='" + name + "'";
            }else{
                strSql = strSql + " and o.machineName = '" + name + "'";
            }
        }
        if(strSql.isEmpty()){
            strSql = "select o from MachineInfo o";
        }else{
            strSql = "select o from MachineInfo o where " + strSql;
        }
         return (List<MachineInfo>) findHSQL(strSql);
    }
    

}
