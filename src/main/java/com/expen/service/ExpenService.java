package com.expen.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expen.repository.PaymentRepository;

@Service
public class ExpenService {

	@Autowired
	PaymentRepository paymentRepository;
	
	public BigDecimal getExpenditure(LocalDateTime startTime, LocalDateTime endTime, String email) {
		return paymentRepository.calculateTotalExpenditure(startTime, endTime, email);
	}
}