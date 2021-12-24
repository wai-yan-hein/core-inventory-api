package com.cv.inv.api.service;

import com.cv.inv.api.entity.Pattern;
import com.cv.inv.api.entity.PatternDetail;

import java.util.List;

public interface PatternService {
    Pattern findByCode(String code);

    Pattern save(Pattern pattern);

    PatternDetail save(PatternDetail pd);

    List<Pattern> search(String compCode, Boolean active);

    List<PatternDetail> searchDetail(String code) throws Exception;

}
