package com.heroku.reference.archiveagent.api;

import com.heroku.reference.archiveagent.domain.Invoice;
import com.heroku.reference.archiveagent.domain.InvoiceLine;
import com.heroku.reference.archiveagent.repositories.InvoiceLineRepository;
import com.heroku.reference.archiveagent.repositories.InvoiceRepository;
import com.heroku.reference.archiveagent.util.DatabaseInfo;
import com.heroku.reference.archiveagent.util.DocumentParser;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.*;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Tag(name = "Archive Agent API", description = "Process natural language queries for archived information")
@RestController
public class QueryService {

    private static final Logger logger = LoggerFactory.getLogger(QueryService.class);

    final private EntityManager entityManager;
    final private DataSource dataSource;
    final private InvoiceRepository invoiceRepository;
    final private InvoiceLineRepository invoiceLineRepository;
    final private ChatModel chatModel;

    @Autowired
    public QueryService(
            EntityManager entityManager,
            DataSource dataSource,
            InvoiceRepository invoiceRepository,
            InvoiceLineRepository invoiceLineRepository,
            ChatModel chatModel) {
        this.entityManager = entityManager;
        this.dataSource = dataSource;
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineRepository = invoiceLineRepository;
        this.chatModel = chatModel;
    }

    @PostMapping("/process") @Transactional
    public QueryResponse processQuery(@RequestBody QueryRequest request) {

        // Parse newly archived invoices into the db (for production this could be a worker thread)
        List<Invoice> invoices = new ArrayList<>();
        List<InvoiceLine> invoiceLines = new ArrayList<>();
        DocumentParser.parseInvoices(invoiceRepository, invoices, invoiceLines);
        invoiceRepository.saveAll(invoices);
        invoiceLineRepository.saveAll(invoiceLines);

        // Create prompt to generate the SQL based on the DB schema and the users prompt
        String prompt = """
            Given the following database schema, remove SQL syntax from the query and generate a SQL query.
            Please observe the following requests.
            Only return on field or calculated field value in the outer select statement.
            Constrain the SQL query only to INVOICE and INVOICELINE tables
            Only return the SQL no explanation, without the sql prefix.
            Use a SQL function TO_CHAR to format numeric values, without leading spaces or thousand separators.
            Schema: %s
            User Query: %s.
            """.formatted(DatabaseInfo.getSchema(dataSource), request.query);
        String generatedSQL = chatModel.call(prompt);
        logger.info("Prompt used {}", prompt);
        logger.info("Query generated {}", generatedSQL);

        // Run AI generated SQL query
        Query sqlQuery = entityManager.createNativeQuery(generatedSQL);
        @SuppressWarnings("unchecked")
        List<Object[]> resultList = sqlQuery.getResultList();
        QueryResponse response = new QueryResponse();
        response.result = ""+resultList;
        logger.info("Result is {}", resultList);
        return response;
    }

    // Input for query operation
    public static class QueryRequest {
        @Schema(example = "What is the total invoice amount for all invoices?")
        public String query;
    }
    // Output for query operation
    public static class QueryResponse {
        public String result;
    }
}


