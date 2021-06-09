/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;


import com.cv.inv.api.entity.CompoundKey;
import com.cv.inv.api.entity.VouId;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lenovo
 */
@Repository
public class VouIdDaoImpl extends AbstractDao<String, VouId> implements VouIdDao {

    @Override
    public VouId save(VouId vouId) {
        persist(vouId);
        return vouId;
    }

    @Override
    public Object getMax(String machineName, String vouType, String vouPeriod) throws Exception {

        String strSQl = "select max(o.vouNo) from VouId o where o.key.machineName='" 
                + machineName + "' and  o.key.vouType='" + vouType + "' and o.key.period ='" + vouPeriod + "'";
        Object obj = exeSQL(strSQl);
        return obj;
    }

    @Override
    public Object find(CompoundKey key) {
        Object obj = findByKey(VouId.class, key);
        return obj;
        }
    
      @Override
    public List<VouId> search(String machineName, String vouType, String period) {
        String strSql = "";

        if (!machineName.equals("-")) {
            if (strSql.isEmpty()) {
                strSql = "o.key.machineName = '" + machineName + "'";
            } else {
                strSql = strSql + " and o.key.machineName = '" + machineName + "'";
            }
        }

        if (!vouType.equals("-")) {
            if (strSql.isEmpty()) {
                strSql = "o.key.vouType = '" + vouType + "'";
            } else {
                strSql = strSql + " and o.key.vouType = '" + vouType + "'";
            }
        }

        if (!period.equals("-")) {
            if (strSql.isEmpty()) {
                strSql = "o.key.period = '" + period + "'";
            } else {
                strSql = strSql + " and o.key.period = '" + period + "'";
            }
        }

        if (strSql.isEmpty()) {
            strSql = "select o from VouId o";
        } else {
            strSql = "select o from VouId o where " + strSql;
        }

        List<VouId> listVI = findHSQL(strSql);
        return listVI;
    }

}
