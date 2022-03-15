package com.demo.recon.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ReconStmtRequest {

	private String sender;

	private String receiver;

	private Long transactionRef;

	private Long accountNo;

	private Long statementNo;
	
	private String txnCurrency;

	private List<ReconStmtTxnDetails> reconTxnDetails = new ArrayList<>();

}
