package com.heroku.reference.archiveagent.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "invoiceline")
public class InvoiceLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unitprice", nullable = false)
    private Double unitPrice;

    @Column(name = "totalprice", nullable = false)
    private Double totalPrice;

    @ManyToOne
    @JoinColumn(name = "invoiceId")
    private Invoice invoice;

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String product) {
        this.description = product;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
