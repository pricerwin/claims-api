package com.priceerwin.claims.claim;

import com.priceerwin.claims.claim.ClaimDtos.ClaimResponse;
import com.priceerwin.claims.claim.ClaimDtos.CreateClaimRequest;
import com.priceerwin.claims.common.BusinessRuleException;
import com.priceerwin.claims.common.NotFoundException;
import com.priceerwin.claims.policy.Policy;
import com.priceerwin.claims.policy.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;

    // Constructor injection: Spring passes in the repositories, and tests can pass mocks.
    public ClaimService(ClaimRepository claimRepository, PolicyRepository policyRepository) {
        this.claimRepository = claimRepository;
        this.policyRepository = policyRepository;
    }

    @Transactional
    public ClaimResponse fileClaim(CreateClaimRequest request) {
        Policy policy = policyRepository.findById(request.policyId())
                .orElseThrow(() -> new NotFoundException("Policy " + request.policyId() + " not found"));

        // Business rule 1: the incident must happen while the policy is active.
        if (!policy.isActiveOn(request.incidentDate())) {
            throw new BusinessRuleException(
                    "Incident date " + request.incidentDate() + " is outside the policy's coverage period");
        }

        // Business rule 2: a claim can't ask for more than the coverage limit.
        if (request.amountRequested().compareTo(policy.getCoverageLimit()) > 0) {
            throw new BusinessRuleException(
                    "Requested amount exceeds coverage limit of " + policy.getCoverageLimit());
        }

        Claim claim = new Claim(policy, request.incidentDate(), request.description(), request.amountRequested());
        return ClaimResponse.from(claimRepository.save(claim));
    }

    @Transactional(readOnly = true)
    public ClaimResponse getClaim(Long id) {
        return ClaimResponse.from(findClaim(id));
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> listClaims(ClaimStatus status) {
        List<Claim> claims = (status == null) ? claimRepository.findAll() : claimRepository.findByStatus(status);
        return claims.stream().map(ClaimResponse::from).toList();
    }

    // Business rule 3: status changes must follow the lifecycle (enforced in Claim/ClaimStatus).
    @Transactional
    public ClaimResponse updateStatus(Long id, ClaimStatus next) {
        Claim claim = findClaim(id);
        claim.transitionTo(next);
        return ClaimResponse.from(claim);
    }

    private Claim findClaim(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Claim " + id + " not found"));
    }
}
