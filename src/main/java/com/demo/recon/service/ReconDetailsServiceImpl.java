package com.demo.recon.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.recon.entity.CurrencyClauseMapping;
import com.demo.recon.entity.CurrencyDetails;
import com.demo.recon.entity.ReconAuditDetails;
import com.demo.recon.entity.ReconDetails;
import com.demo.recon.entity.ReconMatchedTransactions;
import com.demo.recon.model.ReconStmtRequest;
import com.demo.recon.repository.CurrencyClauseMappingRepository;
import com.demo.recon.repository.CurrencyDetailsRepository;
import com.demo.recon.repository.ReconMatchedTransactionsRepository;
import com.demo.recon.repository.ReconRepository;
import com.demo.recon.repository.ReconRepositoryImpl;

@Service
public class ReconDetailsServiceImpl {

	private static final String MATCHED = "MATCHED";
	private static final String RECONCILE_SUCCESSFUL = "Reconcile Successful";

	@Autowired
	private ReconRepository reconRepository;

	@Autowired
	private ReconRepositoryImpl reconRepositoryImpl;

	@Autowired
	private ReconMatchedTransactionsRepository reconMatchedTransactionsRepository;

	@Autowired
	private ReconAuditServiceImpl reconAuditServiceImpl;

	@Autowired
	private TransactionValidatorServiceImpl transactionValidatorServiceImpl;

	@Autowired
	private CurrencyClauseMappingRepository currencyClauseMappingRepository;

	@Autowired
	private CurrencyDetailsRepository currencyDetailsRepository;

	public List<ReconDetails> acceptStmtAndReconcile(ReconStmtRequest reconStmtRequest, String uid) {
		transactionValidatorServiceImpl.isReconStmtRequestValid(reconStmtRequest);
		List<ReconAuditDetails> reconAuditDetailsListGenerated = reconAuditServiceImpl
				.auditReconStatementReceived(reconStmtRequest, uid);
		List<ReconDetails> validatedTxnList = validateAndReconcile(reconAuditDetailsListGenerated);
		return validatedTxnList;

	}

	public List<ReconDetails> validateAndReconcile(List<ReconAuditDetails> reconAuditDetailsList) {
		// Validation hook
		List<ReconAuditDetails> validatedTxnList = transactionValidatorServiceImpl.validate(reconAuditDetailsList);
		saveReconciledTranStatments(validatedTxnList);
		return reconcile();
	}

	/**
	 * Method to save data in recon details
	 * 
	 * @param reconAuditDetailsList
	 */
	public void saveReconciledTranStatments(List<ReconAuditDetails> reconAuditDetailsList) {

		reconAuditDetailsList.forEach(reconAuditDtl -> {
			ReconDetails reconDetails = new ReconDetails();
			reconDetails.setTransactionRefNumber(reconAuditDtl.getTransactionRefNumber());
			reconDetails.setTxnAmt(reconAuditDtl.getTxnAmt());
			reconDetails.setTxnCurrency(reconAuditDtl.getTxnCurrency());
			reconDetails.setSender(reconAuditDtl.getSender());
			reconDetails.setReceiver(reconAuditDtl.getReceiver());
			reconDetails.setAccountNo(reconAuditDtl.getAccountNo());
			reconDetails.setStmtNumber(reconAuditDtl.getStmtNumber());
			reconDetails.setTransactionStmtRef(reconAuditDtl.getTransactionStmtRef());
			reconDetails.setReconcileStatus(reconAuditDtl.getReconcileStatus());
			reconDetails.setTxnDate(reconAuditDtl.getTxnDate());
			reconDetails.setSource(reconAuditDtl.getSource());
			reconDetails.setDbCrMark(reconAuditDtl.getDbCrMark());
			reconDetails.setUid(reconAuditDtl.getUid());
			reconRepository.save(reconDetails);
		});

	}

	/**
	 * Method for reconcile
	 * 
	 * @return
	 */
	public List<ReconDetails> reconcile() {
		List<ReconDetails> conciledTranactionsList = new ArrayList<>();
		// List<Object[]> records = reconRepository.findMatchedTransactions();

		List<CurrencyClauseMapping> currencyClauseMappingList = currencyClauseMappingRepository.findAll();

		Map<Long, List<CurrencyClauseMapping>> currencyClauseMappingMap = currencyClauseMappingList.stream()
				.collect(Collectors.groupingBy(CurrencyClauseMapping::getCurrencyId));
		List<String> inputClauseList = new ArrayList<>();

		currencyClauseMappingMap.forEach((currId, currencyClauseMapping) -> {
			currencyClauseMapping.forEach(currencyClause -> {
				inputClauseList.add(currencyClause.getClause());
			});
			CurrencyDetails currencyDetails = currencyDetailsRepository.findById(currId).get();

			List<Object[]> records = reconRepositoryImpl.reconcile(inputClauseList, currencyDetails.getCurrencyName());

			List<Long> idList = new ArrayList<>();
			records.forEach(obj -> {

				// Code to store txn mapping
				ReconMatchedTransactions reconMatchedTransactions = new ReconMatchedTransactions();
				reconMatchedTransactions.setTransactionRefNumber1(Long.parseLong(obj[0].toString()));
				reconMatchedTransactions.setTransactionRefNumber2(Long.parseLong(obj[1].toString()));

				reconMatchedTransactionsRepository.save(reconMatchedTransactions);

				// List of transaction ids which are reconciled and matched
				for (Object txnId : obj) {
					idList.add(Long.parseLong(txnId.toString()));

				}
			});

			// Update status for each transaction as MATCHED
			idList.forEach(txnId -> {
				ReconDetails reconDetails = new ReconDetails();
				reconDetails = reconRepository.findById(txnId).get();
				reconDetails.setReconcileStatus(MATCHED);
				reconRepository.save(reconDetails);
				conciledTranactionsList.add(reconDetails);
			});

		});
		reconAuditServiceImpl.updateAuditReconcileStatus(conciledTranactionsList, MATCHED, RECONCILE_SUCCESSFUL, "");

		/*
		 * inputClauseList.add("txn_amt"); inputClauseList.add("txn_currency");
		 */

		return conciledTranactionsList;
	}

}