package com.heroku.reference.archiveagent.repositories;

import com.heroku.reference.archiveagent.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    // JPQL query to get existing invoice numbers in a single query
    @Query("SELECT i.invoiceNumber FROM Invoice i WHERE i.invoiceNumber IN (:invoiceNumbers)")
    Set<String> findExistingInvoiceNumbers(Set<String> invoiceNumbers);
}
