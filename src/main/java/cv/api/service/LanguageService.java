package cv.api.service;


import cv.api.entity.Language;
import cv.api.entity.LanguageKey;
import cv.api.entity.OrderStatus;
import cv.api.entity.OrderStatusKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface LanguageService {
    Language save(Language status);
    List<Language> findAll(String compCode);
    int delete(LanguageKey key);
    Language findById(LanguageKey key);
    List<Language> search(String description);
    List<Language> unUpload();

    Date getMaxDate();

    List<Language> getLanguage(LocalDateTime updatedDate);
}
