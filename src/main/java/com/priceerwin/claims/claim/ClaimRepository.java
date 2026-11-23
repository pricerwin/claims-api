package com.priceerwin.claims.claim;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    // Spring Data builds the query from the method name.
    List<Claim> findByPolicyId(Long policyId);

    List<Claim> findByStatus(ClaimStatus status);
}
