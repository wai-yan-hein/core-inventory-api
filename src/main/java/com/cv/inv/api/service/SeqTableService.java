/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.SeqKey;
import com.cv.inv.api.entity.SeqTable;

import java.util.List;

/**
 *
 * @author winswe
 */
 public interface SeqTableService {

     SeqTable save(SeqTable st);

     SeqTable findById(SeqKey id);

     List<SeqTable> search(String option, String period, String compCode);

     SeqTable getSeqTable(String option, String period, String compCode);

     int delete(Integer id);

     int getSequence(Integer macId,String option, String period, String compCode);

     List<SeqTable> findAll();
}
