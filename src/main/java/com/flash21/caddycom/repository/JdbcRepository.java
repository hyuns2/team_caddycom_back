package com.flash21.caddycom.repository;

import java.util.List;

public interface JdbcRepository<T> {
    List<Long> saveAllInBatch(List<T> entities);
}
