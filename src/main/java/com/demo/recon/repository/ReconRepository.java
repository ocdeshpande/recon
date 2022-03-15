package com.demo.recon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.demo.recon.constants.ReconSource;
import com.demo.recon.entity.ReconDetails;

@Repository
public interface ReconRepository extends JpaRepository<ReconDetails, Long>, JpaSpecificationExecutor<ReconDetails> {

	@Query
	List<ReconDetails> findBySource(ReconSource source);

	@Query(value = "SELECT\r\n" + "   t1.id as ID1, t2.id as ID2\r\n" + "FROM\r\n" + "    devrecon.tb_recon_dtls t1\r\n"
			+ "INNER JOIN devrecon.tb_recon_dtls t2 ON t1.id > t2.id\r\n" + "AND t1.db_cr_mark = t2.db_cr_mark \r\n"
			+ "AND t1.txn_amt = t2.txn_amt\r\n" + "AND t1.txn_currency = t2.txn_currency\r\n"
			+ "AND t1.txn_date = t2.txn_date\r\n" + "AND t1.source != t2.source\r\n"
			+ "AND t1.reconcile_status ='UNMATCHED' AND  t2.reconcile_status ='UNMATCHED'\r\n"
			+ "AND t1.transaction_ref_number = t2.transaction_ref_number;", nativeQuery = true)
	List<Object[]> findMatchedTransactions();

}
