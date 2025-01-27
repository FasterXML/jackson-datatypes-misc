package com.fasterxml.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.javamoney.moneta.CurrencyUnitBuilder;
import org.javamoney.moneta.Money;
import org.junit.Test;

import javax.money.CurrencyUnit;

import static org.assertj.core.api.Assertions.assertThat;


public final class CurrencyUnitSerializerTest {

    private final ObjectMapper unit = JsonMapper.builder().addModule(new JavaxMoneyModule()).build();

    @Test
    public void shouldSerialize() throws JsonProcessingException {
        final String expected = "EUR";
        final CurrencyUnit currency = CurrencyUnitBuilder.of(expected, "default").build();

        final String actual = unit.writeValueAsString(currency);

        assertThat(actual).isEqualTo('"' + expected + '"');
    }

}
