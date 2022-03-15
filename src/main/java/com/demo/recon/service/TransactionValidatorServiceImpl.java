package com.demo.recon.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.recon.entity.CurrencyDetails;
import com.demo.recon.entity.ReconAuditDetails;
import com.demo.recon.exception.InvalidRequestException;
import com.demo.recon.model.ReconStmtRequest;
import com.demo.recon.repository.BankSystemDetailsRepository;
import com.demo.recon.repository.CurrencyDetailsRepository;
import com.demo.recon.repository.ReconAuditDetailsRepository;
import com.demo.recon.repository.SettlementSystemDetailsRepository;

@Service
public class TransactionValidatorServiceImpl {

	private static final String INCORRECT = "Incorrect";

	private static final String INVALID_CURRENCY = "Invalid Currency";

	private static final String INVALID_SENDER = "Invalid Sender for given currency";

	private static final String INVALID_RECEIVER = "Invalid Reciver for given currency";

	@Autowired
	private CurrencyDetailsRepository currencyDetailsRepository;

	@Autowired
	private ReconAuditDetailsRepository reconAuditDetailsRepository;

	@Autowired
	private SettlementSystemDetailsRepository settlementSystemDetailsRepository;

	@Autowired
	private BankSystemDetailsRepository bankSystemDetailsRepository;

	/**
	 * Method to validate each request parameter
	 * 
	 * @param reconStmtRequest
	 * @return
	 */
	public boolean isReconStmtRequestValid(ReconStmtRequest reconStmtRequest) {
		if (Objects.isNull(reconStmtRequest)) {
			throw new InvalidRequestException("Invalid Request");
		} else if (Objects.isNull(reconStmtRequest.getAccountNo())) {
			throw new InvalidRequestException("Invalid Account Number");
		} else if (Objects.isNull(reconStmtRequest.getReceiver())) {
			throw new InvalidRequestException("Invalid Receiver");
		} else if (Objects.isNull(reconStmtRequest.getSender())) {
			throw new InvalidRequestException("Invalid Sender");
		} else if (Objects.isNull(reconStmtRequest.getStatementNo())) {
			throw new InvalidRequestException("Invalid Statement Number");
		} else if (Objects.isNull(reconStmtRequest.getTransactionRef())) {
			throw new InvalidRequestException("Invalid Statement Transaction Reference");
		} else if (Objects.isNull(reconStmtRequest.getReconTxnDetails())
				|| reconStmtRequest.getReconTxnDetails().isEmpty()) {
			throw new InvalidRequestException("Invalid Transaction Details");
		} else if (Objects.isNull(reconStmtRequest.getTxnCurrency())) {
			throw new InvalidRequestException(INVALID_CURRENCY);
		}

		reconStmtRequest.getReconTxnDetails().forEach(reconTxn -> {
			if (Objects.isNull(reconTxn.getTransactionRefNumber())) {
				throw new InvalidRequestException("Invalid Transaction Reference");
			} else if (Objects.isNull(reconTxn.getDbCrMark())) {
				throw new InvalidRequestException("Invalid Db/Cr");
			} else if (Objects.isNull(reconTxn.getSource())) {
				throw new InvalidRequestException("Invalid Source");
			} else if (Objects.isNull(reconTxn.getTxnAmt())) {
				throw new InvalidRequestException("Invalid Transaction Amount");
			} else if (Objects.isNull(reconTxn.getTxnDate())) {
				throw new InvalidRequestException("Invalid Transaction Date");
			}
		});

		return true;
	}

	/**
	 * Validate Method for currency/sender/receiver details
	 * 
	 * @param reconAuditDetailsList
	 * @return
	 */
	public List<ReconAuditDetails> validate(List<ReconAuditDetails> reconAuditDetailsList) {
		List<ReconAuditDetails> validatedTxnList = new ArrayList<>();
		validatedTxnList = validateCurrency(reconAuditDetailsList);
		validatedTxnList = validateSender(validatedTxnList);
		validatedTxnList = validateReciver(validatedTxnList);
		return validatedTxnList;
	}

	/**
	 * Method to validate sender
	 * 
	 * @param reconAuditDetailsList
	 * @return
	 */
	public List<ReconAuditDetails> validateSender(List<ReconAuditDetails> reconAuditDetailsList) {
		List<ReconAuditDetails> validatedTxnList = new ArrayList<>();
		reconAuditDetailsList.forEach(reconAuditDetail -> {
			CurrencyDetails currency = fetchCurrency(reconAuditDetail);
			if (settlementSystemDetailsRepository
					.findByOrganizationBICAndCurrencyId(reconAuditDetail.getSender(), currency.getId()).isEmpty()) {
				reconAuditDetail.setReason(INVALID_SENDER);
				reconAuditDetail.setStatus(INCORRECT);
				reconAuditDetailsRepository.save(reconAuditDetail);
			} else {
				validatedTxnList.add(reconAuditDetail);
			}
		});

		return validatedTxnList;
	};

	/**
	 * Method to validate receiver
	 * 
	 * @param reconAuditDetailsList
	 * @return
	 */
	public List<ReconAuditDetails> validateReciver(List<ReconAuditDetails> reconAuditDetailsList) {
		List<ReconAuditDetails> validatedTxnList = new ArrayList<>();
		reconAuditDetailsList.forEach(reconAuditDetail -> {
			CurrencyDetails currency = fetchCurrency(reconAuditDetail);
			if (bankSystemDetailsRepository.findByBankBICAndCurrencyId(reconAuditDetail.getReceiver(), currency.getId())
					.isEmpty()) {
				reconAuditDetail.setReason(INVALID_RECEIVER);
				reconAuditDetail.setStatus(INCORRECT);
				reconAuditDetailsRepository.save(reconAuditDetail);
			} else {
				validatedTxnList.add(reconAuditDetail);
			}
		});

		return validatedTxnList;
	};

	/**
	 * Method to validate Currency
	 * 
	 * @param reconAuditDetailsList
	 * @return
	 */
	public List<ReconAuditDetails> validateCurrency(List<ReconAuditDetails> reconAuditDetailsList) {
		List<ReconAuditDetails> validatedTxnList = new ArrayList<>();
		reconAuditDetailsList.forEach(reconAuditDetail -> {
			CurrencyDetails currency = fetchCurrency(reconAuditDetail);
			if (Objects.isNull(currency)) {
				reconAuditDetail.setReason(INVALID_CURRENCY);
				reconAuditDetail.setStatus(INCORRECT);
				reconAuditDetailsRepository.save(reconAuditDetail);
			} else {
				validatedTxnList.add(reconAuditDetail);
			}
		});

		return validatedTxnList;
	}

	private CurrencyDetails fetchCurrency(ReconAuditDetails reconAuditDetail) {
		CurrencyDetails currency = currencyDetailsRepository
				.findByCurrencyNameAndIsCurrencyValid(reconAuditDetail.getTxnCurrency(), true);
		return currency;
	};

}
