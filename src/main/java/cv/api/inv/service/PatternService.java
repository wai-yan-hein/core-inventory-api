package cv.api.inv.service;

import cv.api.inv.entity.Pattern;
import cv.api.inv.entity.PatternDetail;

import java.util.List;

public interface PatternService {
    Pattern findByCode(String code);

    Pattern save(Pattern pattern);

    PatternDetail save(PatternDetail pd);

    List<Pattern> search(String compCode, Boolean active);

    List<PatternDetail> searchDetail(String code) throws Exception;

}
