package com.demo.recon.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

@Repository
public class ReconRepositoryImpl {

	private static final String T2 = "t2.";
	private static final String T1 = "t1.";
	private static final String AND = " AND ";
	private static final String basicQuery = "SELECT t1.id as ID1, t2.id as ID2 FROM devrecon.tb_recon_dtls t1 INNER JOIN devrecon.tb_recon_dtls t2 ON t1.id > t2.id AND t1.source != t2.source AND t1.txn_currency = t2.txn_currency AND t1.reconcile_status ='UNMATCHED' AND  t2.reconcile_status ='UNMATCHED' AND t1.db_cr_mark = t2.db_cr_mark";
	@PersistenceContext
	private EntityManager entityManager;

	public List<Object[]> reconcile(List<String> clauses, String currency) {
		StringBuilder sb = new StringBuilder();
		sb.append(basicQuery);

		clauses.forEach(clause -> {
			if (!clause.isEmpty()) {
				sb.append(AND + T1 + clause + "=" + T2 + clause);
			}
		});
		sb.append(" AND t1.txn_currency = :currencyName");
		Query query = entityManager.createNativeQuery(sb.toString());
		query.setParameter("currencyName", currency);
		return query.getResultList();
	}

}
