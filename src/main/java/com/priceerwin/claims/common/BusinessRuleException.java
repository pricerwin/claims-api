package com.priceerwin.claims.common;

/** Thrown when a request is well-formed but breaks an insurance business rule. */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
