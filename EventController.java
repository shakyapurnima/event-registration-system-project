package com.InvertisAuditoriumManagement.AudiMgmt.controller;

import com.InvertisAuditoriumManagement.AudiMgmt.payloads.EventDto;
import com.InvertisAuditoriumManagement.AudiMgmt.serviceimpl.EventServiceImpl;
import com.InvertisAuditoriumManagement.AudiMgmt.constant.AppConstant;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.EventNotFoundException;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    
    private EventServiceImpl eventServiceImpl;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createEvent(
            @RequestParam("eventName") String eventName,
            @RequestParam("startTime") LocalDate startTime,
            @RequestParam("eventDescription") String eventDescription,
            @RequestParam("banner") MultipartFile banner)  {

        try {
            String response = eventServiceImpl.createEvent(eventName, startTime, eventDescription, banner);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (MaxUploadSizeExceededException e) {
            return new ResponseEntity<>("File size exceeds the maximum limit!", HttpStatus.PAYLOAD_TOO_LARGE);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>("Event name already exists or violates constraints!", HttpStatus.CONFLICT);
        } catch (FileUploadException e) {
            return new ResponseEntity<>("Failed to upload file: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Failed to create event due to an internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
    }

    @GetMapping("/{eventName}")
    
    public ResponseEntity<?> getEventByName(@PathVariable String eventName) {
        try {
            EventDto event = eventServiceImpl.getEventByName(eventName);
            return new ResponseEntity<>(event, HttpStatus.OK);
        } catch (EventNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("An error occurred while fetching the event", HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
    }

  
    @GetMapping("/all")
    public ResponseEntity<?> getAllEvents(
            @RequestParam(defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer pageNumber,
            @RequestParam(defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(defaultValue = AppConstant.DEFAULT_SORT_DIRECTION) String sortDir,
            @RequestParam(defaultValue = AppConstant.DEFAULT_SORT_BY) String sortBy) {
        try {
            List<EventDto> events = eventServiceImpl.getAllEvent(pageNumber, pageSize, sortDir, sortBy);
            return new ResponseEntity<>(events, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of()); 
        }
    }

  
    @PutMapping("/update/{eventName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateEvent(
            @PathVariable String eventName,
            @RequestBody EventDto eventDto) {
        try {
            String response = eventServiceImpl.updateEvent(eventDto, eventName);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EventNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Failed to update event due to an internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
    }

    
    @DeleteMapping("/delete/{eventName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEvent(@PathVariable String eventName) {
        try {
            String response = eventServiceImpl.deleteEvent(eventName);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EventNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Failed to delete event due to an internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
    }
}
