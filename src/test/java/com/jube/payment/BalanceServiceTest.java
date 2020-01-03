package com.jube.payment;

import com.jube.payment.balance.BalanceService;
import com.jube.payment.balance.api.BalanceDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

public class BalanceServiceTest {

    @Test
    public void testGetBalance_accountWithNoTransactions_returnsZeroBalance() {
        BalanceService balanceService = new BalanceService();
        BalanceDto balanceDto = balanceService.getBalance("no-entries-account");
        Assertions.assertEquals(BigDecimal.ZERO, balanceDto.getBalance());
    }

    @Test
    public void testGetBalance_accountWithTransactions_returnsCorrectBalance() {
        BalanceService balanceService = new BalanceService();
        BalanceDto balanceDto = balanceService.getBalance("acc-with-money");
        Assertions.assertEquals(BigDecimal.valueOf(235100, 2), balanceDto.getBalance());
    }

    @Test
    public void testGetBalances_balancesReturned() {
        BalanceService balanceService = new BalanceService();
        List<BalanceDto> balanceDtos = balanceService.getBalances();

        Assertions.assertTrue(balanceDtos.size() > 0);
    }
}