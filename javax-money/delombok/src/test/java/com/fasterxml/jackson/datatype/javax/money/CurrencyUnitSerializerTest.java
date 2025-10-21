package com.fasterxml.jackson.datatype.javax.money;

import javax.money.CurrencyUnit;

import org.javamoney.moneta.CurrencyUnitBuilder;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;


public final class CurrencyUnitSerializerTest {

    private final ObjectMapper unit = JsonMapper.builder().addModule(new JavaxMoneyModule()).build();

    @Test
    public void shouldSerialize() throws Exception {
        final String expected = "EUR";
        final CurrencyUnit currency = CurrencyUnitBuilder.of(expected, "default").build();

        final String actual = unit.writeValueAsString(currency);

        assertThat(actual).isEqualTo('"' + expected + '"');
    }

}
