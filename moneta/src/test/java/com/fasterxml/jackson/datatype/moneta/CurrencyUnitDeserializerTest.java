package com.fasterxml.jackson.datatype.moneta;

import javax.money.CurrencyUnit;
import javax.money.UnknownCurrencyException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import org.javamoney.moneta.CurrencyUnitBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class CurrencyUnitDeserializerTest {

    private final ObjectMapper unit = JsonMapper.builder().addModule(new MonetaMoneyModule()).build();

    @Test
    public void shouldDeserialize() throws Exception {
        final CurrencyUnit actual = unit.readValue("\"EUR\"", CurrencyUnit.class);
        final CurrencyUnit expected = CurrencyUnitBuilder.of("EUR", "default").build();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldNotDeserializeInvalidCurrency() {
        assertThrows(UnknownCurrencyException.class, () ->
                unit.readValue("\"FOO\"", CurrencyUnit.class));
    }

    @Test
    public void shouldDeserializeWithTyping() throws Exception {
        unit.activateDefaultTyping(BasicPolymorphicTypeValidator.builder().build());

        final CurrencyUnit actual = unit.readValue("\"EUR\"", CurrencyUnit.class);
        final CurrencyUnit expected = CurrencyUnitBuilder.of("EUR", "default").build();

        assertThat(actual).isEqualTo(expected);
    }

}
