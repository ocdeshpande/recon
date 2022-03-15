package com.demo.recon.model;

import java.math.BigDecimal;
import java.util.Date;

import com.demo.recon.constants.ReconSource;
import com.demo.recon.constants.TransactionType;

import lombok.Data;

@Data
public class ReconStmtTxnDetails {

	private Long transactionRefNumber;

	private BigDecimal txnAmt;

	private Date txnDate;

	private ReconSource source;

	private TransactionType dbCrMark;

}
