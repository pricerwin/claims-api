package com.priceerwin.claims.common;

import com.priceerwin.claims.policy.Policy;
import com.priceerwin.claims.policy.PolicyRepository;
import com.priceerwin.claims.policy.PolicyType;
import com.priceerwin.claims.policyholder.Policyholder;
import com.priceerwin.claims.policyholder.PolicyholderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Inserts one sample policyholder and policy on first startup so you can file
 * claims right away. Once you build the Policy endpoints (see README), you can delete this.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final PolicyholderRepository policyholderRepository;
    private final PolicyRepository policyRepository;

    public DataSeeder(PolicyholderRepository policyholderRepository, PolicyRepository policyRepository) {
        this.policyholderRepository = policyholderRepository;
        this.policyRepository = policyRepository;
    }

    @Override
    public void run(String... args) {
        if (policyRepository.count() > 0) {
            return;
        }
        Policyholder holder = policyholderRepository.save(
                new Policyholder("Jane", "Doe", "jane.doe@example.com"));
        policyRepository.save(new Policy(
                "AUTO-1001", PolicyType.AUTO, new BigDecimal("10000.00"),
                LocalDate.now().minusMonths(6), LocalDate.now().plusMonths(6), holder));
    }
}
