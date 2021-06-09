/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.ChargeType;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Repository
public class ChargeTypeDaoImpl extends AbstractDao<String, ChargeType> implements ChargeTypeDao {

    @Override
    public ChargeType save(ChargeType chargeType) {
        persist(chargeType);
        return chargeType;
    }

    @Override
    public List<ChargeType> findAll() {
        String hsql = "select o from ChargeType o";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from ChargeType o where o.chargeTypeId='" + id + "'";
        return execUpdateOrDelete(hsql);
    }
    
      @Override
    public List<ChargeType> search(String ctId, String desp){
        String strFilter = "";
        
        
        if(!desp.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.chargeTypeId =" + desp ;
            }else{
                strFilter = strFilter + " and o.chargeTypeId =" + desp ;
            }
        }
        if(!desp.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.chargeTypeDesp like '%" + desp + "%'";
            }else{
                strFilter = strFilter + " and o.chargeTypeDesp like '%" + desp + "%'";
            }
        }
        
       /* if(!updatedDate.equals("-")){
            if(strFilter.isEmpty()){
                strFilter = "o.updatedDate = '" + updatedDate + "'";
            }else{
                strFilter = strFilter + " and o.updatedDate = '" + updatedDate + "'";
            }
        }
        */
        if(strFilter.isEmpty()){
            strFilter = "select o from ChargeType o";
        }else{
            strFilter = "select o from ChargeType o where " + strFilter;
        }
        
        List<ChargeType> listCT = findHSQL(strFilter);
        return listCT;
    }

}
