package com.fasterxml.jackson.datatype.moneta;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.money.MonetaryAmount;

@AllArgsConstructor
@Getter
public class SchemaTestClass {

    private final MonetaryAmount moneyOne;
    private final MonetaryAmount moneyTwo;

}
