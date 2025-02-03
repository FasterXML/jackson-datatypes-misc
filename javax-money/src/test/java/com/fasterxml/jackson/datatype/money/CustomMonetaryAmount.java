package com.fasterxml.jackson.datatype.money;

import org.javamoney.moneta.FastMoney;
import org.jetbrains.annotations.NotNull;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryAmountFactory;
import javax.money.MonetaryContext;
import javax.money.MonetaryOperator;
import javax.money.MonetaryQuery;
import javax.money.NumberValue;

class CustomMonetaryAmount implements MonetaryAmount {
    private final FastMoney delegate;

    public CustomMonetaryAmount(FastMoney delegate) {
        this.delegate = delegate;
    }

    public int getPrecision() {
        return delegate.getPrecision();
    }

    public boolean isLessThan(Number number) {
        return delegate.isLessThan(number);
    }

    public boolean isGreaterThanOrEqualTo(Number number) {
        return delegate.isGreaterThanOrEqualTo(number);
    }

    public int getScale() {
        return delegate.getScale();
    }

    public boolean hasSameNumberAs(Number number) {
        return delegate.hasSameNumberAs(number);
    }

    public boolean isLessThanOrEqualTo(Number number) {
        return delegate.isLessThanOrEqualTo(number);
    }

    public boolean isGreaterThan(Number number) {
        return delegate.isGreaterThan(number);
    }

    @Override
    public MonetaryContext getContext() {
        return delegate.getContext();
    }

    @Override
    public MonetaryAmountFactory<? extends MonetaryAmount> getFactory() {
        return delegate.getFactory();
    }

    @Override
    public boolean isGreaterThan(MonetaryAmount amount) {
        return delegate.isGreaterThan(amount);
    }

    @Override
    public boolean isGreaterThanOrEqualTo(MonetaryAmount amount) {
        return delegate.isGreaterThanOrEqualTo(amount);
    }

    @Override
    public boolean isLessThan(MonetaryAmount amount) {
        return delegate.isLessThan(amount);
    }

    @Override
    public boolean isLessThanOrEqualTo(MonetaryAmount amt) {
        return delegate.isLessThanOrEqualTo(amt);
    }

    @Override
    public boolean isEqualTo(MonetaryAmount amount) {
        return delegate.isEqualTo(amount);
    }

    @Override
    public int signum() {
        return delegate.signum();
    }

    @Override
    public MonetaryAmount add(MonetaryAmount augend) {
        return delegate.add(augend);
    }

    @Override
    public MonetaryAmount subtract(MonetaryAmount subtrahend) {
        return delegate.subtract(subtrahend);
    }

    @Override
    public MonetaryAmount multiply(long multiplicand) {
        return delegate.multiply(multiplicand);
    }

    @Override
    public MonetaryAmount multiply(double multiplicand) {
        return delegate.multiply(multiplicand);
    }

    @Override
    public MonetaryAmount multiply(Number multiplicand) {
        return delegate.multiply(multiplicand);
    }

    @Override
    public MonetaryAmount divide(long divisor) {
        return delegate.divide(divisor);
    }

    @Override
    public MonetaryAmount divide(double divisor) {
        return delegate.divide(divisor);
    }

    @Override
    public MonetaryAmount divide(Number divisor) {
        return delegate.divide(divisor);
    }

    @Override
    public MonetaryAmount remainder(long divisor) {
        return delegate.remainder(divisor);
    }

    @Override
    public MonetaryAmount remainder(double divisor) {
        return delegate.remainder(divisor);
    }

    @Override
    public MonetaryAmount remainder(Number divisor) {
        return delegate.remainder(divisor);
    }

    @Override
    public MonetaryAmount[] divideAndRemainder(long divisor) {
        return delegate.divideAndRemainder(divisor);
    }

    @Override
    public MonetaryAmount[] divideAndRemainder(double divisor) {
        return delegate.divideAndRemainder(divisor);
    }

    @Override
    public MonetaryAmount[] divideAndRemainder(Number divisor) {
        return delegate.divideAndRemainder(divisor);
    }

    @Override
    public MonetaryAmount divideToIntegralValue(long divisor) {
        return delegate.divideToIntegralValue(divisor);
    }

    @Override
    public MonetaryAmount divideToIntegralValue(double divisor) {
        return delegate.divideToIntegralValue(divisor);
    }

    @Override
    public MonetaryAmount divideToIntegralValue(Number divisor) {
        return delegate.divideToIntegralValue(divisor);
    }

    @Override
    public MonetaryAmount scaleByPowerOfTen(int power) {
        return delegate.scaleByPowerOfTen(power);
    }

    @Override
    public boolean isZero() {
        return delegate.isZero();
    }

    @Override
    public boolean isPositive() {
        return delegate.isPositive();
    }

    @Override
    public boolean isPositiveOrZero() {
        return delegate.isPositiveOrZero();
    }

    @Override
    public boolean isNegative() {
        return delegate.isNegative();
    }

    @Override
    public boolean isNegativeOrZero() {
        return delegate.isNegativeOrZero();
    }

    @Override
    public MonetaryAmount abs() {
        return delegate.abs();
    }

    @Override
    public MonetaryAmount negate() {
        return delegate.negate();
    }

    @Override
    public MonetaryAmount plus() {
        return delegate.plus();
    }

    @Override
    public MonetaryAmount stripTrailingZeros() {
        return delegate.stripTrailingZeros();
    }

    @Override
    public int compareTo(@NotNull MonetaryAmount o) {
        return delegate.compareTo(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    @Override
    public CurrencyUnit getCurrency() {
        return delegate.getCurrency();
    }

    @Override
    public NumberValue getNumber() {
        return delegate.getNumber();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public FastMoney with(MonetaryOperator operator) {
        return delegate.with(operator);
    }

    @Override
    public <R> R query(MonetaryQuery<R> query) {
        return delegate.query(query);
    }
}