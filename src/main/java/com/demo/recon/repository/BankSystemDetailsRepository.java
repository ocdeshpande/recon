package com.demo.recon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.recon.entity.BankSystemDetails;

@Repository
public interface BankSystemDetailsRepository extends JpaRepository<BankSystemDetails, Long> {

	List<BankSystemDetails> findByBankBICAndCurrencyId(String sender, Long currencyId);
}
