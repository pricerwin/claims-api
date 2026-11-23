package com.priceerwin.claims.claim;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request/response shapes for the claims API. Keeping these separate from the
 * entity means the database model can change without breaking API clients.
 */
public final class ClaimDtos {

    private ClaimDtos() {}

    public record CreateClaimRequest(
            @NotNull Long policyId,
            @NotNull @PastOrPresent LocalDate incidentDate,
            @NotBlank String description,
            @NotNull @DecimalMin(value = "0.01") BigDecimal amountRequested
    ) {}

    public record ClaimResponse(
            Long id,
            Long policyId,
            String policyNumber,
            LocalDate incidentDate,
            String description,
            BigDecimal amountRequested,
            ClaimStatus status
    ) {
        public static ClaimResponse from(Claim claim) {
            return new ClaimResponse(
                    claim.getId(),
                    claim.getPolicy().getId(),
                    claim.getPolicy().getPolicyNumber(),
                    claim.getIncidentDate(),
                    claim.getDescription(),
                    claim.getAmountRequested(),
                    claim.getStatus());
        }
    }
}
