package com.demo.recon.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handle(Exception ex, HttpServletRequest request, HttpServletResponse response) {
		if (ex instanceof NullPointerException) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<Object> handleInvalidRequestException(Exception ex, HttpServletRequest request,
			HttpServletResponse response) {
		if (ex instanceof NullPointerException) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

}