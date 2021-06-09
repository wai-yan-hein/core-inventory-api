/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.SeqKey;
import com.cv.inv.api.entity.SeqTable;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class SeqTableDaoImpl extends AbstractDao<SeqKey, SeqTable> implements SeqTableDao {

    @Override
    public SeqTable save(SeqTable st) {
        persist(st);
        return st;
    }

    @Override
    public SeqTable findById(SeqKey id) {
        SeqTable st = getByKey(id);
        return st;
    }

    @Override
    public List<SeqTable> search(String option, String period, String compCode) {
        String strSql = "select o from SeqTable o where o.seqOption = '" + option
                + "' and o.compCode = '" + compCode + "'";

        if (!period.equals("-")) {
            strSql = strSql + " and o.period = '" + period + "'";
        }

        List<SeqTable> listST = findHSQL(strSql);
        return listST;
    }

    @Override
    public SeqTable getSeqTable(String option, String period, String compCode) {
        List<SeqTable> listST = search(option, period, compCode);
        SeqTable st = null;

        if (listST != null) {
            if (!listST.isEmpty()) {
                st = listST.get(0);
            }
        }

        return st;
    }

    @Override
    public int delete(Integer id) {
        String strSql = "delete from SeqTable o where o.id = " + id;
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }

    @Override
    public int getSequence(Integer macId, String option, String period, String compCode) {
        SeqKey key = new SeqKey();
        key.setCompCode(compCode);
        key.setMacId(macId);
        key.setPeriod(period);
        key.setSeqOption(option);
        SeqTable st = findById(key);
        if (st == null) {
            st=new SeqTable();
            st.setKey(key);
            st.setSeqNo(1);
        } else {
            st.setSeqNo(st.getSeqNo() + 1);
        }
        save(st);
        int seq = st.getSeqNo();
        return seq;
    }

    @Override
    public List<SeqTable> findAll() {
        String strSql = "select o from SeqTable o";
        List<SeqTable> ListSTAB = findHSQL(strSql);
        return ListSTAB;
    }
}
