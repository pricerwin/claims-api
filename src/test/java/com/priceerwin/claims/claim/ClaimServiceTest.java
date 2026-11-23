package com.priceerwin.claims.claim;

import com.priceerwin.claims.claim.ClaimDtos.ClaimResponse;
import com.priceerwin.claims.claim.ClaimDtos.CreateClaimRequest;
import com.priceerwin.claims.common.BusinessRuleException;
import com.priceerwin.claims.common.NotFoundException;
import com.priceerwin.claims.policy.Policy;
import com.priceerwin.claims.policy.PolicyRepository;
import com.priceerwin.claims.policy.PolicyType;
import com.priceerwin.claims.policyholder.Policyholder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private ClaimService claimService;

    private Policy policy;

    @BeforeEach
    void setUp() {
        Policyholder holder = new Policyholder("Test", "User", "test@example.com");
        policy = new Policy("AUTO-1001", PolicyType.AUTO, new BigDecimal("10000.00"),
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), holder);
    }

    @Test
    void fileClaim_succeedsWhenWithinCoverageAndPeriod() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(claimRepository.save(any(Claim.class))).thenAnswer(inv -> inv.getArgument(0));

        ClaimResponse response = claimService.fileClaim(new CreateClaimRequest(
                1L, LocalDate.of(2026, 6, 15), "Rear-ended at a stoplight", new BigDecimal("2500.00")));

        assertThat(response.status()).isEqualTo(ClaimStatus.SUBMITTED);
        assertThat(response.policyNumber()).isEqualTo("AUTO-1001");
    }

    @Test
    void fileClaim_rejectsAmountOverCoverageLimit() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        assertThatThrownBy(() -> claimService.fileClaim(new CreateClaimRequest(
                1L, LocalDate.of(2026, 6, 15), "Total loss", new BigDecimal("15000.00"))))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("coverage limit");

        verify(claimRepository, never()).save(any());
    }

    @Test
    void fileClaim_rejectsIncidentOutsidePolicyPeriod() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        assertThatThrownBy(() -> claimService.fileClaim(new CreateClaimRequest(
                1L, LocalDate.of(2025, 12, 31), "Hail damage", new BigDecimal("500.00"))))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("coverage period");
    }

    @Test
    void fileClaim_throwsWhenPolicyMissing() {
        when(policyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> claimService.fileClaim(new CreateClaimRequest(
                99L, LocalDate.of(2026, 6, 15), "Anything", new BigDecimal("100.00"))))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void claimStatus_followsLifecycle() {
        Claim claim = new Claim(policy, LocalDate.of(2026, 6, 15), "Test", new BigDecimal("100.00"));

        claim.transitionTo(ClaimStatus.UNDER_REVIEW);
        claim.transitionTo(ClaimStatus.APPROVED);
        claim.transitionTo(ClaimStatus.PAID);

        assertThat(claim.getStatus()).isEqualTo(ClaimStatus.PAID);
    }

    @Test
    void claimStatus_cannotSkipReview() {
        Claim claim = new Claim(policy, LocalDate.of(2026, 6, 15), "Test", new BigDecimal("100.00"));

        assertThatThrownBy(() -> claim.transitionTo(ClaimStatus.APPROVED))
                .isInstanceOf(BusinessRuleException.class);
    }
}
