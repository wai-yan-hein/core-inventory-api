/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.SeqKey;
import com.cv.inv.api.entity.SeqTable;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface SeqTableDao {

    public SeqTable save(SeqTable st);

    public SeqTable findById(SeqKey id);

    public List<SeqTable> search(String option, String period, String compCode);

    public SeqTable getSeqTable(String option, String period, String compCode);

    public int delete(Integer id);

    public int getSequence(Integer macId,String option, String period, String compCode);

    public List<SeqTable> findAll();
}
