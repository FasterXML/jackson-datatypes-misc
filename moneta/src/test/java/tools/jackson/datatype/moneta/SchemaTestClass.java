package tools.jackson.datatype.moneta;

import javax.money.MonetaryAmount;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SchemaTestClass {
    private final MonetaryAmount moneyOne;
    private final MonetaryAmount moneyTwo;
}
