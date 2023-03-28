package com.cts.onlineShopping.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.ExpiredJwtException;

@RestControllerAdvice
@CrossOrigin(origins = "*")
public class ControlAdvice {
	
	@ExceptionHandler(value = {
			ExpiredJwtException.class
	})
	public ResponseEntity<String> tokenExpired(){
		return new ResponseEntity<String>("Session Expired, Login Again", HttpStatus.FORBIDDEN);
	}
	
}
