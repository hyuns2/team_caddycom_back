package com.flash21.caddycom.repository.assignment;

import java.time.LocalTime;
import java.util.List;

public interface AssignmentJdbcRepository {
    void bulkInsert(Long scheduleId, List<LocalTime> startTimeList);
}
