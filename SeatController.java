package com.InvertisAuditoriumManagement.AudiMgmt.controller;


import com.InvertisAuditoriumManagement.AudiMgmt.constant.AppConstant;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.SeatNotFoundException;
import com.InvertisAuditoriumManagement.AudiMgmt.payloads.SeatDto;
import com.InvertisAuditoriumManagement.AudiMgmt.service.SeatService;
import com.InvertisAuditoriumManagement.AudiMgmt.serviceimpl.SeatServiceImpl;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatServiceImpl seatServiceImpl;

    public SeatController(SeatServiceImpl seatServiceImpl) {
        this.seatServiceImpl = seatServiceImpl;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createSeat(@RequestBody SeatDto seatDto) {
        try {
            String response = seatServiceImpl.createSeats(seatDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
    }

    @PutMapping("/{seatNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateSeat(@RequestBody SeatDto seatDto, @PathVariable String seatNumber) {
        try {
            String response = seatServiceImpl.updateSeat(seatDto, seatNumber);
            return ResponseEntity.ok(response);
        } catch (SeatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
    }

    @DeleteMapping("/{seatNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteSeat(@PathVariable String seatNumber) {
        try {
            String response = seatServiceImpl.deleteSeat(seatNumber);
            return ResponseEntity.ok(response);
        } catch (SeatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
    }

    @GetMapping
    public ResponseEntity<List<SeatDto>> getAllSeats(
            @RequestParam(defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer pageNumber,
            @RequestParam(defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(defaultValue = AppConstant.DEFAULT_SORT_DIRECTION) String sortDir,
            @RequestParam(defaultValue = AppConstant.DEFAULT_SORT_BY) String sortBy) {
        try {
            List<SeatDto> seats = seatServiceImpl.getAllSeats(pageNumber, pageSize, sortDir, sortBy);
            if (seats.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(seats);
            }
            return ResponseEntity.ok(seats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of()); 
        }
    }
    @GetMapping("/{seatNumber}")
    public ResponseEntity<?> getBySeatNumber(@PathVariable String seatNumber) {
        try {
            SeatDto seatDto = seatServiceImpl.getBySeatNumber(seatNumber);
            return ResponseEntity.ok(seatDto);
        } catch (SeatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
    }
    @PostMapping("/bulk-create")
    public void createSeats(@RequestBody List<SeatDto> seatDtos) {
        seatServiceImpl.createBulkSeat(seatDtos);
    }

}
