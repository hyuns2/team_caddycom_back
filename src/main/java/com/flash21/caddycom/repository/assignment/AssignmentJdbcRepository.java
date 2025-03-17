package com.flash21.caddycom.repository.assignment;

import com.flash21.caddycom.entity.schedule.Assignment;

import java.util.List;

public interface AssignmentJdbcRepository {
    void bulkInsert(List<Assignment> assignments);
}
