package com.priceerwin.claims.claim;

import com.priceerwin.claims.common.BusinessRuleException;
import com.priceerwin.claims.policy.Policy;
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
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id")
    private Policy policy;

    @Column(nullable = false)
    private LocalDate incidentDate;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amountRequested;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status;

    protected Claim() {
        // required by JPA
    }

    public Claim(Policy policy, LocalDate incidentDate, String description, BigDecimal amountRequested) {
        this.policy = policy;
        this.incidentDate = incidentDate;
        this.description = description;
        this.amountRequested = amountRequested;
        this.status = ClaimStatus.SUBMITTED;
    }

    /** Moves the claim to a new status, enforcing the allowed lifecycle. */
    public void transitionTo(ClaimStatus next) {
        if (!status.canMoveTo(next)) {
            throw new BusinessRuleException(
                    "Cannot move claim " + id + " from " + status + " to " + next);
        }
        this.status = next;
    }

    public Long getId() { return id; }
    public Policy getPolicy() { return policy; }
    public LocalDate getIncidentDate() { return incidentDate; }
    public String getDescription() { return description; }
    public BigDecimal getAmountRequested() { return amountRequested; }
    public ClaimStatus getStatus() { return status; }
}
