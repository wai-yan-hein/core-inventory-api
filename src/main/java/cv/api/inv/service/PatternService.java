package cv.api.inv.service;

import cv.api.inv.entity.Pattern;

import java.util.List;

public interface PatternService {
    Pattern findByCode(String code);

    Pattern save(Pattern pattern);

    List<Pattern> search(String stockCode);
    void delete(String stockCode);

}
