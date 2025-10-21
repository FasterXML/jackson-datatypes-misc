package tools.jackson.datatype.javax.money;

import javax.money.MonetaryAmount;

public class SchemaTestClass {
    private final MonetaryAmount moneyOne;
    private final MonetaryAmount moneyTwo;

    //@lombok.Generated
    public SchemaTestClass(final MonetaryAmount moneyOne, final MonetaryAmount moneyTwo) {
        this.moneyOne = moneyOne;
        this.moneyTwo = moneyTwo;
    }

    //@lombok.Generated
    public MonetaryAmount getMoneyOne() {
        return this.moneyOne;
    }

    //@lombok.Generated
    public MonetaryAmount getMoneyTwo() {
        return this.moneyTwo;
    }
}
