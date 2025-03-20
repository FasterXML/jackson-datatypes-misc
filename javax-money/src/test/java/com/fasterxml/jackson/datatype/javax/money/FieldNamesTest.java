package com.fasterxml.jackson.datatype.javax.money;


import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.datatype.javax.money.FieldNames;

import static org.assertj.core.api.Assertions.assertThat;

public final class FieldNamesTest {

    @Test
    public void shouldOptimizeWithMethods() {
        final FieldNames expected = FieldNames.defaults();
        final FieldNames actual = expected
                .withAmount(expected.getAmount())
                .withCurrency(expected.getCurrency())
                .withFormatted(expected.getFormatted());

        assertThat(actual).isSameAs(expected);
    }

}
