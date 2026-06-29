package com.eventrouting.upstream.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventrouting.upstream.dto.TransactionRequest;
import com.eventrouting.upstream.service.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TransactionController {

	private final TransactionService transactionService;

	@PostMapping("/transactions")
	public ResponseEntity<Void> initiateTransaction(
			@RequestBody TransactionRequest request,
			@RequestParam(name = "count", defaultValue = "1") int count) {
		for (int i = 0; i < count; i++) {
			transactionService.initiateTransaction(request);
		}
		return ResponseEntity.accepted().build();
	}

}
