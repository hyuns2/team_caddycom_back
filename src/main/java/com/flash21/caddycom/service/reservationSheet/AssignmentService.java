package com.flash21.caddycom.service.reservationSheet;

import com.flash21.caddycom.dto.reservationSheet.AssignmentDto;
import com.flash21.caddycom.entity.reservationSheet.Assignment;
import com.flash21.caddycom.entity.reservationSheet.AssignmentStatus;
import com.flash21.caddycom.entity.reservationSheet.ReservationDate;
import com.flash21.caddycom.entity.reservationSheet.ReservationSheet;
import com.flash21.caddycom.global.exception.cException.CReservationDateNotFoundException;
import com.flash21.caddycom.global.exception.cException.CReservationSheetNotFoundException;
import com.flash21.caddycom.repository.reservationSheet.AssignmentRepository;
import com.flash21.caddycom.repository.reservationSheet.ReservationDateRepository;
import com.flash21.caddycom.repository.reservationSheet.ReservationSheetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentService {
    final ReservationSheetRepository rsRepository;
    final ReservationDateRepository rdRepository;
    final AssignmentRepository assignmentRepository;

    public List<AssignmentDto.AssignmentsResponse> retrieveAssignments(Long reservationSheetId, LocalDate targetDate) {
        Optional<ReservationDate> result = rdRepository.findByReservationSheetIdAndReservationAt(reservationSheetId, targetDate);
        if (result.isEmpty())
            throw new CReservationDateNotFoundException();

        ReservationDate reservationDate = result.get();
        if (reservationDate.getIsAssigned()) {
            return findAndRetrieveAssignments(reservationDate);
        }

        Optional<ReservationSheet> reservationSheet = rsRepository.findById(reservationSheetId);
        if (reservationSheet.isEmpty())
            throw new CReservationSheetNotFoundException();
        return createAndRetrieveAssignments(reservationSheet.get(), reservationDate);
    }

    private List<AssignmentDto.AssignmentsResponse> findAndRetrieveAssignments(ReservationDate reservationDate) {
        List<AssignmentDto.AssignmentsResponse> responseDtoList = new ArrayList<>();
        List<Assignment> assignmentList = assignmentRepository.findAllByReservationDateId(reservationDate.getId());

        for (Assignment assignment : assignmentList)
            responseDtoList.add(toDto(assignment));

        return responseDtoList;
    }

    private List<AssignmentDto.AssignmentsResponse> createAndRetrieveAssignments(ReservationSheet reservationSheet, ReservationDate reservationDate) {
        List<AssignmentDto.AssignmentsResponse> responseDtoList = new ArrayList<>();
        LocalTime startAtLocalTime = reservationSheet.getStartAt().toLocalTime();
        LocalTime endAtLocalTIme = reservationSheet.getEndAt().toLocalTime();

        List<Integer> teeOffList = Arrays.stream(reservationSheet.getTeeOff().split("~")).
                map(Integer::new).toList();
        int teeOffListSize = teeOffList.size();
        int currentTeeOffIndex = 0;

        while (startAtLocalTime.isBefore(endAtLocalTIme)) {
            Assignment assignment = assignmentRepository.save(Assignment.builder().
                    reservationDate(reservationDate).
                    startTime(startAtLocalTime).
                    status(AssignmentStatus.NOTHING).
                    // caddyId().
                    caddyName(null).
                    reason(null).build());
            responseDtoList.add(toDto(assignment));

            startAtLocalTime = startAtLocalTime.plusMinutes(teeOffList.get(currentTeeOffIndex++));
            if (currentTeeOffIndex >= teeOffListSize)
                currentTeeOffIndex = 0;
        }

        reservationDate.setIsAssigned();
        reservationDate.setTotalCnt(responseDtoList.size());
        rdRepository.save(reservationDate);
        return responseDtoList;
    }

    private AssignmentDto.AssignmentsResponse toDto(Assignment assignment) {
        return AssignmentDto.AssignmentsResponse.builder().
                id(assignment.getId()).
                reservationDate(assignment.getReservationDate().getReservationAt()).
                startTime(assignment.getStartTime()).
                status(assignment.getStatus()).
                // caddyId(assignment.getCaddyId()).
                caddyName(assignment.getCaddyName()).
                reason(assignment.getReason()).build();
    }
}
