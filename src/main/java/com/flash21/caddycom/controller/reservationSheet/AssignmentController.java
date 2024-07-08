package com.flash21.caddycom.controller.reservationSheet;

import com.flash21.caddycom.dto.reservationSheet.ReservationSheetDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "3-2. Reservation Sheet", description = "예약시트 API")
public class AssignmentController {

}
