package com.demo.recon.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.recon.entity.InputAuditMessage;
import com.demo.recon.entity.ReconAuditDetails;
import com.demo.recon.entity.ReconDetails;
import com.demo.recon.model.ReconStmtRequest;
import com.demo.recon.model.ReconStmtTxnDetails;
import com.demo.recon.repository.InputAuditMessageRepository;
import com.demo.recon.repository.ReconAuditDetailsRepository;

@Service
public class ReconAuditServiceImpl {

	private static final String RECEIVED = "Received";
	private static final String UNMATCHED = "UNMATCHED";
	private static final String MATCHED = "MATCHED";

	@Autowired
	private ReconAuditDetailsRepository reconAuditDetailsRepository;

	@Autowired
	private InputAuditMessageRepository inputAuditMessageRepository;

	/**
	 * Method to audit data received from transaction statement
	 * 
	 * @param reconStmtRequest
	 * @param uniqueStamentNo
	 * @return
	 */
	public List<ReconAuditDetails> auditReconStatementReceived(ReconStmtRequest reconStmtRequest,
			String uniqueStamentNo) {
		auditOriginalMsg(reconStmtRequest, uniqueStamentNo);
		List<ReconAuditDetails> reconAuditDetailsList = new ArrayList<>();
		List<ReconAuditDetails> reconAuditDetailsListGenerated = new ArrayList<>();
		reconStmtRequest.getReconTxnDetails().forEach(reconTxn -> {
			ReconAuditDetails reconAuditDetails = populateAuditReconDetails(reconStmtRequest, uniqueStamentNo,
					reconTxn);
			reconAuditDetailsList.add(reconAuditDetails);
		});

		reconAuditDetailsList.forEach(reconAuditDetails -> {
			reconAuditDetailsListGenerated.add(reconAuditDetailsRepository.save(reconAuditDetails));
		});

		return reconAuditDetailsListGenerated;
	}

	/**
	 * Method to audit data received from transaction statement
	 * 
	 * @param reconStmtRequest
	 * @param uniqueStamentNo
	 * @return
	 */
	public void auditOriginalMsg(ReconStmtRequest reconStmtRequest, String uniqueStamentNo) {
		InputAuditMessage inputAuditMessage = new InputAuditMessage();
		inputAuditMessage.setUid(uniqueStamentNo);
		inputAuditMessage.setJsonContent(reconStmtRequest);
		inputAuditMessageRepository.save(inputAuditMessage);

	}

	/**
	 * Update status of the reconciliation for a transaction in audit table
	 * 
	 * @param conciledTranactionsList
	 */
	public void updateAuditReconcileStatus(List<ReconDetails> conciledTranactionsList, String reconcileStatus,
			String status, String reason) {
		conciledTranactionsList.forEach(recondDtl -> {
			List<ReconAuditDetails> reconAuditDetailsList = reconAuditDetailsRepository
					.findByTransactionRefNumber(recondDtl.getTransactionRefNumber());
			reconAuditDetailsList.forEach(reconAuditDetails -> {
				reconAuditDetails.setReconcileStatus(reconcileStatus);
				reconAuditDetails.setStatus(status);
				reconAuditDetails.setReason(reason);
				reconAuditDetailsRepository.save(reconAuditDetails);
			});

		});
	}

	private ReconAuditDetails populateAuditReconDetails(ReconStmtRequest reconStmtRequest, String uniqueStamentNo,
			ReconStmtTxnDetails reconTxn) {
		ReconAuditDetails reconAuditDetails = new ReconAuditDetails();
		reconAuditDetails.setTransactionRefNumber(reconTxn.getTransactionRefNumber());
		reconAuditDetails.setTxnAmt(reconTxn.getTxnAmt());
		reconAuditDetails.setTxnCurrency(reconStmtRequest.getTxnCurrency());
		reconAuditDetails.setSender(reconStmtRequest.getSender());
		reconAuditDetails.setReceiver(reconStmtRequest.getReceiver());
		reconAuditDetails.setAccountNo(reconStmtRequest.getAccountNo());
		reconAuditDetails.setStmtNumber(reconStmtRequest.getStatementNo());
		reconAuditDetails.setTransactionStmtRef(reconStmtRequest.getTransactionRef());
		reconAuditDetails.setReconcileStatus(UNMATCHED);
		reconAuditDetails.setTxnDate(reconTxn.getTxnDate());
		reconAuditDetails.setSource(reconTxn.getSource());
		reconAuditDetails.setDbCrMark(reconTxn.getDbCrMark());
		reconAuditDetails.setUid(uniqueStamentNo);
		reconAuditDetails.setStatus(RECEIVED);
		return reconAuditDetails;
	}

}