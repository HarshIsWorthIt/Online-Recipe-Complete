package com.recipely.repository;

import com.recipely.model.BaseEntity;
import java.util.List;
import java.util.Optional;

/** Generic repository contract used by JDBC data-access classes. */
public interface Repository<T extends BaseEntity> {
    List<T> findAll();
    Optional<T> findById(long id);
    T save(T entity);
}
