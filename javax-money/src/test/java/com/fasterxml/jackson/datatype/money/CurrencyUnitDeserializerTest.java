package com.fasterxml.jackson.datatype.money;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.javamoney.moneta.CurrencyUnitBuilder;
import org.junit.Test;

import javax.money.CurrencyUnit;
import javax.money.UnknownCurrencyException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

public final class CurrencyUnitDeserializerTest {

    private final ObjectMapper unit = new ObjectMapper().registerModule(new MoneyModule());

    @Test
    public void shouldDeserialize() throws IOException {
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
    public void shouldDeserializeWithTyping() throws IOException {
        unit.activateDefaultTyping(BasicPolymorphicTypeValidator.builder().build());

        final CurrencyUnit actual = unit.readValue("\"EUR\"", CurrencyUnit.class);
        final CurrencyUnit expected = CurrencyUnitBuilder.of("EUR", "default").build();

        assertThat(actual).isEqualTo(expected);
    }

}
