package com.demo.recon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.recon.entity.CurrencyDetails;

@Repository
public interface CurrencyDetailsRepository extends JpaRepository<CurrencyDetails, Long> {

	CurrencyDetails findByCurrencyNameAndIsCurrencyValid(String currencyName, boolean isCurrencyValid);

}
