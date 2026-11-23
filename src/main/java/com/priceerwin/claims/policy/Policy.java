package com.priceerwin.claims.policy;

import com.priceerwin.claims.policyholder.Policyholder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String policyNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyType type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal coverageLimit;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policyholder_id")
    private Policyholder policyholder;

    protected Policy() {
        // required by JPA
    }

    public Policy(String policyNumber, PolicyType type, BigDecimal coverageLimit,
                  LocalDate startDate, LocalDate endDate, Policyholder policyholder) {
        this.policyNumber = policyNumber;
        this.type = type;
        this.coverageLimit = coverageLimit;
        this.startDate = startDate;
        this.endDate = endDate;
        this.policyholder = policyholder;
    }

    /** A policy is active on a date if the date falls within its coverage period (inclusive). */
    public boolean isActiveOn(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public Long getId() { return id; }
    public String getPolicyNumber() { return policyNumber; }
    public PolicyType getType() { return type; }
    public BigDecimal getCoverageLimit() { return coverageLimit; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Policyholder getPolicyholder() { return policyholder; }
}
