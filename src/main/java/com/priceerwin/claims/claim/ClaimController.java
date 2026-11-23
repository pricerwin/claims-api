package com.priceerwin.claims.claim;

import com.priceerwin.claims.claim.ClaimDtos.ClaimResponse;
import com.priceerwin.claims.claim.ClaimDtos.CreateClaimRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClaimResponse fileClaim(@Valid @RequestBody CreateClaimRequest request) {
        return claimService.fileClaim(request);
    }

    @GetMapping("/{id}")
    public ClaimResponse getClaim(@PathVariable Long id) {
        return claimService.getClaim(id);
    }

    @GetMapping
    public List<ClaimResponse> listClaims(@RequestParam(required = false) ClaimStatus status) {
        return claimService.listClaims(status);
    }

    @PostMapping("/{id}/review")
    public ClaimResponse startReview(@PathVariable Long id) {
        return claimService.updateStatus(id, ClaimStatus.UNDER_REVIEW);
    }

    @PostMapping("/{id}/approve")
    public ClaimResponse approve(@PathVariable Long id) {
        return claimService.updateStatus(id, ClaimStatus.APPROVED);
    }

    @PostMapping("/{id}/deny")
    public ClaimResponse deny(@PathVariable Long id) {
        return claimService.updateStatus(id, ClaimStatus.DENIED);
    }

    @PostMapping("/{id}/pay")
    public ClaimResponse pay(@PathVariable Long id) {
        return claimService.updateStatus(id, ClaimStatus.PAID);
    }
}
