package com.heroku.reference.archiveagent.util;

import com.heroku.reference.archiveagent.domain.Invoice;
import com.heroku.reference.archiveagent.domain.InvoiceLine;
import com.heroku.reference.archiveagent.repositories.InvoiceRepository;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentParser {

    private static final Logger logger = LoggerFactory.getLogger(DocumentParser.class);

    // Check /invoices folder for archived invoices not in the db and ingest them
    public static void parseInvoices(InvoiceRepository invoiceRepository, List<Invoice> invoices, List<InvoiceLine> invoiceLines) {

        // Parse latest .docx files in the folder
        Pattern invoiceNumberPattern = Pattern.compile("INV-\\d+");
        Set<String> invoiceNumbersToCheck = new HashSet<>();
        File folder = new File("./invoices");
        // Check if the folder exists and is a directory
        if (folder.exists() && folder.isDirectory()) {
            // Obtain a list of invoice numbers in the folder
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                if (file.isFile() && file.getName().endsWith(".docx")) {
                    Matcher invoiceNumnberMatcher = invoiceNumberPattern.matcher(file.getName());
                    if(invoiceNumnberMatcher.find()) {
                        invoiceNumbersToCheck.add((invoiceNumnberMatcher.group()));
                    }
                }
            }
            // Are any of these invoices already in the DB?
            Set<String> existingInvoiceNumbers = invoiceRepository.findExistingInvoiceNumbers(invoiceNumbersToCheck);
            // Iterate over each file in the folder process invoices not yet in the database
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                if (file.isFile() && file.getName().endsWith(".docx")) {
                    // Process each .docx file
                    try (FileInputStream fis = new FileInputStream(file)) {
                        Matcher invoiceNumnberMatcher = invoiceNumberPattern.matcher(file.getName());
                        if(invoiceNumnberMatcher.find()) {
                            String invoiceNumber = invoiceNumnberMatcher.group();
                            if(existingInvoiceNumbers.contains(invoiceNumber)) {
                                continue;
                            }
                            XWPFDocument document = new XWPFDocument(fis);
                            Invoice parsedInvoice = extractInvoiceDetails(document);
                            List<InvoiceLine> parsedInvoiceLines = extractInvoiceLines(parsedInvoice, document);
                            invoices.add(parsedInvoice);
                            invoiceLines.addAll(parsedInvoiceLines);
                            invoiceNumbersToCheck.add(parsedInvoice.getInvoiceNumber());
                            logger.info("Processed {}", file.getName());
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }
    }

    // Extract invoice details from docx file and map to Invoice and InvoiceLine entities
    private static Invoice extractInvoiceDetails(XWPFDocument document) throws ParseException {
        Invoice invoice = new Invoice();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            String text = paragraph.getText().trim();
            if (text.startsWith("Invoice Number:")) {
                invoice.setInvoiceNumber(text.split(":")[1].trim());
            } else if (text.startsWith("Date:")) {
                invoice.setDate(dateFormat.parse(text.split(":")[1].trim()));
            } else if (text.startsWith("Bill To:")) {
                invoice.setBillTo(text.substring(8).trim());
            } else if (text.startsWith("Total Amount:")) {
                invoice.setTotalPrice(Double.parseDouble(text.split(":")[1].replace("$", "").replace(",", "").trim()));
            } else if (text.startsWith("Due Date:")) {
                invoice.setDueDate(dateFormat.parse(text.split(":")[1].trim()));
            }
        }
        return invoice;
    }

    // Extract invoice lines from the DOCX table and map to InvoiceLine entities
    private static List<InvoiceLine> extractInvoiceLines(Invoice invoice, XWPFDocument document) {
        List<InvoiceLine> invoiceLines = new ArrayList<>();
        List<XWPFTable> tables = document.getTables();
        if (!tables.isEmpty()) {
            XWPFTable table = tables.get(0);  // Assuming the first table contains invoice lines
            for (int i = 1; i < table.getRows().size(); i++) {  // Skip the first row (header)
                XWPFTableRow row = table.getRow(i);
                InvoiceLine lineItem = new InvoiceLine();
                lineItem.setInvoice(invoice);
                lineItem.setDescription(row.getCell(0).getText().trim());
                lineItem.setQuantity(Integer.parseInt(row.getCell(1).getText().trim()));
                lineItem.setUnitPrice(Double.parseDouble(row.getCell(2).getText().trim().replace("$", "").replace(",", "")));
                lineItem.setTotalPrice(Double.parseDouble(row.getCell(3).getText().trim().replace("$", "").replace(",", "")));
                invoiceLines.add(lineItem);
            }
        }
        return invoiceLines;
    }

}
