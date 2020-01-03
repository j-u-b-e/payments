package com.jube.payment;

import com.jube.payment.payment.api.PaymentRequestDto;
import com.jube.payment.payment.api.PaymentValidator;
import com.jube.payment.payment.exception.PaymentValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.math.BigDecimal;

public class ValidatorTest {

    @Test
    void testMissingPaymentBody_ValidationExceptionThrown(){
        Executable executable = () -> PaymentValidator.validateTransferRequest(null);

        Assertions.assertThrows(PaymentValidationException.class, executable);
    }

    @Test
    void testNotPositiveAmountInPayment_ValidationExceptionThrown(){
        PaymentRequestDto paymentRequestDto = createPaymentDto();
        paymentRequestDto.setAmount(BigDecimal.valueOf(-123));

        Executable executable = () -> PaymentValidator.validateTransferRequest(paymentRequestDto);

        Assertions.assertThrows(PaymentValidationException.class, executable);
    }

    @Test
    void testMissingFromAccountNumberInPayment_ValidationExceptionThrown(){
        PaymentRequestDto paymentRequestDto = createPaymentDto();
        paymentRequestDto.setFromAccountNumber(null);

        Executable executable = () -> PaymentValidator.validateTransferRequest(paymentRequestDto);

        Assertions.assertThrows(PaymentValidationException.class, executable);
    }

    @Test
    void testMissingToAccountNumberInPayment_ValidationExceptionThrown(){
        PaymentRequestDto paymentRequestDto = createPaymentDto();
        paymentRequestDto.setToAccountNumber(null);

        Executable executable = () -> PaymentValidator.validateTransferRequest(paymentRequestDto);

        Assertions.assertThrows(PaymentValidationException.class, executable);
    }

    @Test
    void testNullPaymentReference_ValidationExceptionThrown(){
        PaymentRequestDto paymentRequestDto = createPaymentDto();
        paymentRequestDto.setReference(null);

        Executable executable = () -> PaymentValidator.validateTransferRequest(paymentRequestDto);

        Assertions.assertThrows(PaymentValidationException.class, executable);
    }

    @Test
    void testEmptyPaymentReference_ValidationExceptionThrown(){
        PaymentRequestDto paymentRequestDto = createPaymentDto();
        paymentRequestDto.setReference("");

        Executable executable = () -> PaymentValidator.validateTransferRequest(paymentRequestDto);

        Assertions.assertThrows(PaymentValidationException.class, executable);
    }

    @Test
    void testFromAndToAccountsMatch_ValidationExceptionThrown(){
        PaymentRequestDto paymentRequestDto = createPaymentDto();
        paymentRequestDto.setFromAccountNumber(paymentRequestDto.getToAccountNumber());

        Executable executable = () -> PaymentValidator.validateTransferRequest(paymentRequestDto);

        Assertions.assertThrows(PaymentValidationException.class, executable);
    }

    protected PaymentRequestDto createPaymentDto(){
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setAmount(BigDecimal.valueOf(123));
        paymentRequestDto.setFromAccountNumber("FROM_ACCOUNT");
        paymentRequestDto.setToAccountNumber("TO_ACCOUNT");
        paymentRequestDto.setReference("REFERENCE");
        return paymentRequestDto;
    }
}
