package com.expen.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expen.service.ExpenService;

@RestController
public class ExpenController {

	@Autowired
	ExpenService expenService;
	
	public ResponseEntity<?> getExpenditure(
			@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam String email
			) {
		
		BigDecimal expenditure = this.expenService.getExpenditure(startTime, endTime, email);
		if (expenditure!=null)
			return ResponseEntity.status(HttpStatus.OK).body(expenditure);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
	}
	
}