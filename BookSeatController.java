package com.InvertisAuditoriumManagement.AudiMgmt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.InvertisAuditoriumManagement.AudiMgmt.payloads.BookingDto;
import com.InvertisAuditoriumManagement.AudiMgmt.serviceimpl.BookSeatServiceImpl;

import com.InvertisAuditoriumManagement.AudiMgmt.constant.AppConstant;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookSeatController {

    private final BookSeatServiceImpl bookSeatServiceImpl;

    public BookSeatController(BookSeatServiceImpl bookSeatServiceImpl) {
        this.bookSeatServiceImpl = bookSeatServiceImpl;
    }

    @PostMapping("/book")
    public synchronized ResponseEntity<?> bookTicket(@RequestParam("seatNumber") String seatNumber,  @RequestParam("eventName") String eventName) {
        try {
            BookingDto bookingDto = bookSeatServiceImpl.bookTicket(seatNumber,eventName);
            return new ResponseEntity<>(bookingDto, HttpStatus.CREATED);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (EventNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BookingAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (SeatNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (SeatAlreadyBookedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllTickets(
            @RequestParam(defaultValue = AppConstant.DEFAULT_PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstant.DEFAULT_PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstant.DEFAULT_SORT_DIRECTION,required = false) String sortDir,
            @RequestParam(defaultValue = AppConstant.DEFAULT_SORT_DIRECTION, required = false) String sortBy) {
        try {
            List<BookingDto> bookingDtos = bookSeatServiceImpl.getAllTicktes(pageNumber, pageSize, sortDir, sortBy);
            return new ResponseEntity<>(bookingDtos, HttpStatus.OK);
        } catch (BookingNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getTicketById(@PathVariable String userId) {
        try {
            BookingDto bookingDto = bookSeatServiceImpl.getTicketById(userId);
            return new ResponseEntity<>(bookingDto, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BookingNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/cancel/{userId}")
    public ResponseEntity<String> cancelTicket(@PathVariable String userId) {
        try {
            String message = bookSeatServiceImpl.cancelTicket(userId);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BookingNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while canceling the booking.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

