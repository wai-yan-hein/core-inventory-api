package cv.api.dao;

import cv.api.entity.Language;
import cv.api.entity.LanguageKey;
import cv.api.entity.OrderStatus;
import cv.api.entity.OrderStatusKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface LanguageDao {
    Language save(Language language);

    List<Language> findAll(String compCode);

    int delete(LanguageKey key);

    Language findById(LanguageKey id);

    List<Language> search(String des);

    List<Language> unUpload();

    Date getMaxDate();

    List<Language> getLanguage(LocalDateTime updatedDate);
}
