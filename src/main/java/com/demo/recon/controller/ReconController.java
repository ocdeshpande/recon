package com.demo.recon.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.demo.recon.entity.ReconDetails;
import com.demo.recon.model.ReconStmtRequest;
import com.demo.recon.service.ReconDetailsServiceImpl;

@RestController
public class ReconController {

	@Autowired
	private ReconDetailsServiceImpl reconDetailsServiceImpl;

	@Autowired
	private HttpServletResponse httpServletResponse;

	/**
	 * Endpoint to receive transactions and reconcile them
	 * 
	 * @param reconStmtRequest
	 * @return
	 */
	@PostMapping("/sendTranStatmentsAndReconcile")
	public ResponseEntity<List<ReconDetails>> sendTranStatmentsAndReconcile(
			@RequestBody ReconStmtRequest reconStmtRequest) {
		String uid = httpServletResponse.getHeader("uniqueStamentNo");

		List<ReconDetails> conciledTranactionsList = reconDetailsServiceImpl.acceptStmtAndReconcile(reconStmtRequest,
				uid);

		return new ResponseEntity<>(conciledTranactionsList, HttpStatus.OK);

	}

	/**
	 * Endpoint to reconcile on demand
	 * 
	 * @return
	 */
	@PostMapping("/reconcile")
	public ResponseEntity<List<ReconDetails>> reconcile() {
		System.out.println("Inside reconcile");

		List<ReconDetails> reconDetailsListConciled = reconDetailsServiceImpl.reconcile();

		return new ResponseEntity<>(reconDetailsListConciled, HttpStatus.OK);

	}

}
