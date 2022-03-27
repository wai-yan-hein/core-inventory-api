package cv.api.inv.dao;

import cv.api.inv.entity.Pattern;
import cv.api.inv.entity.PatternDetail;

import java.util.List;

public interface PatternDao {
    Pattern findByCode(String code);

    Pattern save(Pattern pattern);

    PatternDetail save(PatternDetail pd);

    List<Pattern> search(String compCode, Boolean active);

    List<PatternDetail> searchDetail(String code);

}
