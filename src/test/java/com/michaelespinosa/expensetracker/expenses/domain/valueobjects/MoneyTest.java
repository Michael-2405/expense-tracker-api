package com.michaelespinosa.expensetracker.expenses.domain.valueobjects;

import com.michaelespinosa.expensetracker.expenses.domain.exception.InvalidAmountException;
import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Currency;
import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Money;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {

    @Test
    void shouldCreateValidMoney() {
        Money money = Money.of(new BigDecimal("100.00"), Currency.USD);
        assertEquals(new BigDecimal("100.00"), money.amount());
        assertEquals(Currency.USD, money.currency());
    }

    @Test
    void shouldThrowsWhenAmountIsNull() {
        assertThrows(NullPointerException.class,
                () -> Money.of(null, Currency.USD));
    }

    @Test
    void shouldThrowWhenCurrencyIsNull() {
        assertThrows(NullPointerException.class,
                () -> Money.of(new BigDecimal("100.00"), null));
    }

    @Test
    void shouldThrowWhenAmountIsZero() {
        assertThrows(InvalidAmountException.class,
        () -> Money.of(BigDecimal.ZERO, Currency.USD));
    }

    @Test
    void shouldThrowWhenAmountIsNegative() {
        assertThrows(InvalidAmountException.class,
                () -> Money.of(new BigDecimal("-50.00"), Currency.USD));
    }

    @Test
    void shouldBeEqualWhenSameAmountAndCurrency() {
        Money a = Money.of(new BigDecimal("100.00"), Currency.USD);
        Money b = Money.of(new BigDecimal("100.00"), Currency.USD);
        assertEquals(a, b);
    }

    @Test
    void shouldNotBeEqualWhenDifferentAmount() {
        Money a = Money.of(new BigDecimal("100.00"), Currency.USD);
        Money b = Money.of(new BigDecimal("200.00"), Currency.USD);
        assertNotEquals(a, b);
    }

    @Test
    void shouldNotBeEqualWhenDifferentCurrency() {
        Money a = Money.of(new BigDecimal("100.00"), Currency.USD);
        Money b = Money.of(new BigDecimal("100.00"), Currency.EUR);
        assertNotEquals(a, b);
    }
}
