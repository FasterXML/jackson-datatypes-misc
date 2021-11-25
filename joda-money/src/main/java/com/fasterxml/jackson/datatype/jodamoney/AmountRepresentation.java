package com.fasterxml.jackson.datatype.jodamoney;

/**
 * Enumeration of available strategies used by {@link MoneySerializer} and {@link MoneyDeserializer}
 * to represent amounts of {@link org.joda.money.Money Money}.
 */
public enum AmountRepresentation {

    /**
     * Decimal number representation, where amount is (de)serialized as decimal number equal
     * to {@link org.joda.money.Money Money}'s amount, e.g. {@code 12.34} for
     * {@code Money.parse("EUR 12.34")}.
     */
    DECIMAL_NUMBER,

    /**
     * Decimal string representation, where amount is (de)serialized as string containing decimal
     * number equal to {@link org.joda.money.Money Money}'s amount, e.g. {@code "12.34"} for
     * {@code Money.parse("EUR 12.34")}.
     */
    DECIMAL_STRING,

    /**
     * Minor currency unit representation, where amount is (de)serialized as long integer equal
     * to {@link org.joda.money.Money Money}'s amount expressed in minor currency unit, e.g.
     * {@code 1234} for {@code Money.parse("EUR 12.34")}, {@code 12345} for
     * {@code Money.parse("KWD 12.345")} or {@code 12} for {@code Money.parse("JPY 12")}.
     */
    MINOR_CURRENCY_UNIT,
}
