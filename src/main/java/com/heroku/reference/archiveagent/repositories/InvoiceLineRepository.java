package com.heroku.reference.archiveagent.repositories;

import com.heroku.reference.archiveagent.domain.InvoiceLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceLineRepository extends JpaRepository<InvoiceLine, Long> {
}
