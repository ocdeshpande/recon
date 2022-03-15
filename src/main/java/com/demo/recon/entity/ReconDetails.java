package com.demo.recon.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.demo.recon.constants.ReconSource;
import com.demo.recon.constants.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Entity
@Data
@Table(name = "TB_RECON_DTLS")
public class ReconDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private Long transactionStmtRef;

	private Long accountNo;

	private Long stmtNumber;

	private Long transactionRefNumber;
	
	private String uid;

	private BigDecimal txnAmt;

	private String txnCurrency;

	private String sender;

	private String receiver;
	
	private String reconcileStatus;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date txnDate;

	@Enumerated(EnumType.STRING)
	private ReconSource source;

	@Enumerated(EnumType.STRING)
	private TransactionType dbCrMark;

}
