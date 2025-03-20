package com.fasterxml.jackson.datatype.javax.money;

import org.apiguardian.api.API;

import java.math.BigDecimal;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public interface BigDecimalAmountWriter extends AmountWriter<BigDecimal> {

    @Override
    default Class<BigDecimal> getType() {
        return BigDecimal.class;
    }

}
