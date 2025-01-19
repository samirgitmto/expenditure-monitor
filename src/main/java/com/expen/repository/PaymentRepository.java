package com.expen.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.expen.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

	@Query("SELECT SUM(p.amount) FROM Payment p WHERE p.email=:email AND p.date BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalExpenditure(@Param("startDate") LocalDateTime startDate,
    		@Param("endDate") LocalDateTime endDate, 
    		@Param("email") String email);
	
}