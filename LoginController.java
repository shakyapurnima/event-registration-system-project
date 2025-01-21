package com.InvertisAuditoriumManagement.AudiMgmt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.InvertisAuditoriumManagement.AudiMgmt.globalexception.UserNotFoundException;
import com.InvertisAuditoriumManagement.AudiMgmt.payloads.AuthRequest;
import com.InvertisAuditoriumManagement.AudiMgmt.securityconfig.JwtHelper;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtHelper jwtHelper;

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest) throws UserNotFoundException {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUserId(), authRequest.getPassword())
            );
            if (authenticate.isAuthenticated()) {
                String userId = authRequest.getUserId();
                System.out.println("Authentication successful for userId: " + userId);
                
                String token = jwtHelper.generateToken(userId);
                return token;
            } else {
                System.out.println("Authentication failed for userId: " + authRequest.getUserId());
                throw new BadCredentialsException("Invalid username or password!");
            }
        } catch (BadCredentialsException e) {
            System.err.println("Authentication failed: " + e.getMessage());
            throw new BadCredentialsException("Wrong username or password.");
        } catch (Exception e) {
            System.err.println("Error during authentication: " + e.getMessage());
            throw new UserNotFoundException("Error during authentication: " + e.getMessage());
        }
    }
}

