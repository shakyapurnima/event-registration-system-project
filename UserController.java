package com.InvertisAuditoriumManagement.AudiMgmt.controller;

import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.InvertisAuditoriumManagement.AudiMgmt.constant.AppConstant;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.EmailOrUserIdAlreadyAssociatedException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.InvalidOtpException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.InvalidSortFieldException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.InvalidTokenException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.OtpExpiredException;
import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.UserNotFoundException;
import com.InvertisAuditoriumManagement.AudiMgmt.payloads.AdminSignUp;
import com.InvertisAuditoriumManagement.AudiMgmt.payloads.UserDto;
import com.InvertisAuditoriumManagement.AudiMgmt.serviceimpl.UserServiceImpl;

@RestController
@RequestMapping("/api")
public class UserController {
	private UserServiceImpl userServiceImpl;
	public UserController(UserServiceImpl userServiceImpl) {
		this.userServiceImpl = userServiceImpl;
	}
	@PostMapping("/user/sign-up")
	public ResponseEntity<String> registerUser(@RequestBody UserDto userDto) throws EmailOrUserIdAlreadyAssociatedException{
		 try {
	            String response = userServiceImpl.registerUser(userDto);
	            return new ResponseEntity<>(response, HttpStatus.CREATED);
	        } catch (EmailOrUserIdAlreadyAssociatedException ex) {
	            return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
	        }catch(Exception e) {
	        	return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	        }
		
	}
	 @PostMapping("/forgot-password")
	    public synchronized ResponseEntity<String> forgotPassword(@RequestParam String email) {
	        try {
	            String response = userServiceImpl.forgotPassword(email);
	            return new ResponseEntity<>(response, HttpStatus.OK);
	        } catch (UserNotFoundException e) {
	            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
	        }catch(Exception e) {
	        	return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }

	 @PostMapping("/reset")
	    public ResponseEntity<String> resetPassword(
	            @RequestParam String token,
	            @RequestParam Integer otp,
	            @RequestParam String newPassword) {

	        try {
	            String response = userServiceImpl.resetPassword(token, otp, newPassword);
	            return ResponseEntity.ok(response);
	        } catch (InvalidTokenException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("Invalid token. Please request a new OTP.");
	        } catch (OtpExpiredException e) {
	            return ResponseEntity.status(HttpStatus.GONE)
	                    .body("OTP has expired. Please request a new one.");
	        } catch (InvalidOtpException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body("Invalid OTP. Please try again.");
	        } catch (OptimisticLockingFailureException e) {
	            return ResponseEntity.status(HttpStatus.CONFLICT)
	                    .body("Failed to update password due to a conflict. Please try again.");
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("An unexpected error occurred. Please try again later.");
	        }
	    }
	    @PostMapping("/update-email-request")
	    public ResponseEntity<String> sendOtpForEmailUpdate(@RequestParam String newEmail)throws EmailOrUserIdAlreadyAssociatedException {
	        try {
	            String response = userServiceImpl.sendOtpForEmailUpdate(newEmail);
	            return ResponseEntity.ok(response);
	        } 
	        catch (EmailOrUserIdAlreadyAssociatedException e) {
				return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(e.getMessage());
			}
	        catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("An unexpected error occurred. Please try again later.");
	        }
	    }
	    @PostMapping("/update-email")
	    public ResponseEntity<String> updateEmail(@RequestParam Integer otp) {
	        try {
	            String response = userServiceImpl.updateEmail(otp);
	            return ResponseEntity.ok(response);
	        } catch (InvalidOtpException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        } catch (OtpExpiredException e) {
	            return ResponseEntity.status(HttpStatus.GONE).body(e.getMessage());
	        } catch (UserNotFoundException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	        } catch (Exception e) {
	            // Handle any unexpected exceptions
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("An unexpected error occurred. Please try again later.");
	        }
	    }
	    @GetMapping
	    @PreAuthorize("hasRole('ADMIN')")
	    public ResponseEntity<List<UserDto>> getAllUsers (
	            @RequestParam(value = "pageNumber", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER, required = false) Integer pageNumber,
	            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
	            @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY, required = false) String sortBy,
	            @RequestParam(value = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION, required = false) String sortDir) throws InvalidSortFieldException {
	        
	        try {
	            if (pageNumber < 0) {
	                throw new IllegalArgumentException("Page number must be non-negative.");
	            }
	            if (pageSize <= 0) {
	                throw new IllegalArgumentException("Page size must be greater than zero.");
	            }
	            List<UserDto> response = userServiceImpl.getAllUser(pageNumber, pageSize, sortDir, sortBy);
	            return new ResponseEntity<>(response, HttpStatus.OK);
	        } catch (IllegalArgumentException e) {
	            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	        } catch (InvalidSortFieldException e) {
	            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	        } catch (Exception e) {
	            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	    @PostMapping("/admin/sign-up")
	    public ResponseEntity<String> adminSignUp(@RequestBody AdminSignUp adminSignUp) throws EmailOrUserIdAlreadyAssociatedException{
	    	 try {
		            String response = userServiceImpl.adminSignUp(adminSignUp);
		            return new ResponseEntity<>(response, HttpStatus.CREATED);
		        } catch (EmailOrUserIdAlreadyAssociatedException ex) {
		            return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
		        }catch(Exception e) {
		        	return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		        }
	    }
	    @PostMapping("/bulk-create")
	    public void createUsers(@RequestBody List<UserDto> users) {
	        userServiceImpl.createUsers(users);
	    }
}
