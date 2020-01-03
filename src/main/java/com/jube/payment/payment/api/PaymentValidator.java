package com.jube.payment.payment.api;

import com.jube.payment.payment.exception.PaymentValidationException;
import spark.utils.StringUtils;

import java.math.BigDecimal;

public class PaymentValidator {

    public static void validateTransferRequest(PaymentRequestDto paymentRequestDto) {
        if (paymentRequestDto == null) {
            throw new PaymentValidationException("Missing payment body");
        }
        if (BigDecimal.ZERO.compareTo(paymentRequestDto.getAmount()) >= 0) {
            throw new PaymentValidationException("Amount should be positive");
        }
        if (StringUtils.isEmpty(paymentRequestDto.getFromAccountNumber())) {
            throw new PaymentValidationException("From account must be provided");
        }
        if (StringUtils.isEmpty(paymentRequestDto.getToAccountNumber())) {
            throw new PaymentValidationException("To account must be provided");
        }
        if (paymentRequestDto.getFromAccountNumber().equalsIgnoreCase(paymentRequestDto.getToAccountNumber())) {
            throw new PaymentValidationException("From account can not match to account");
        }
        if (StringUtils.isEmpty(paymentRequestDto.getReference())) {
            throw new PaymentValidationException("Missing payment reference");
        }
    }
}
