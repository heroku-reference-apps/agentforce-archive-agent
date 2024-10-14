package com.heroku.reference.archiveagent.domain;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoiceNumber")
    private String invoiceNumber;

    @Column(name = "billto")
    private String billTo;

    @Column(name = "date")
    private Date date;

    @Column(name = "duedate")
    private Date dueDate;

    @Column(name = "totalprice")
    private Double totalPrice;

    @OneToMany(mappedBy = "invoice")
    private Set<InvoiceLine> lines;

    public Long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public Set<InvoiceLine> getLines() {
        return lines;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getBillTo() {
        return billTo;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setLines(Set<InvoiceLine> lines) {
        this.lines = lines;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setBillTo(String billTo) {
        this.billTo = billTo;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
