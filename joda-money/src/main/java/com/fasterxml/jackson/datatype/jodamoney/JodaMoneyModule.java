package com.fasterxml.jackson.datatype.jodamoney;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import com.fasterxml.jackson.core.Version;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;

public class JodaMoneyModule extends Module
    implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    private final AmountRepresenter<?> amountRepresenter;

    private JodaMoneyModule(AmountRepresenter<?> amountRepresenter) {
        this.amountRepresenter = amountRepresenter;
    }

    public JodaMoneyModule() {
        this(DecimalNumberAmountRepresenter.getInstance());
    }

    @Override
    public String getModuleName() {
        // 05-Mar-2021, tatu: In 2.12 used fully-qualified class name
        //    but standard seems to be moving towards just returning
        //    artifact-id from Version (default by SimpleModule)
        return version().getArtifactId();
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public void setupModule(SetupContext context)
    {
        final SimpleDeserializers desers = new SimpleDeserializers();
        desers.addDeserializer(CurrencyUnit.class, new CurrencyUnitDeserializer());
        desers.addDeserializer(Money.class, new MoneyDeserializer(amountRepresenter));
        context.addDeserializers(desers);

        final SimpleSerializers sers = new SimpleSerializers();
        sers.addSerializer(CurrencyUnit.class, new CurrencyUnitSerializer());
        sers.addSerializer(Money.class, new MoneySerializer(amountRepresenter));
        context.addSerializers(sers);
    }

    public JodaMoneyModule withAmountRepresentation(final AmountRepresentation representation) {
        switch (representation) {
            case DECIMAL_NUMBER:
                return new JodaMoneyModule(DecimalNumberAmountRepresenter.getInstance());
            case DECIMAL_STRING:
                return new JodaMoneyModule(DecimalStringAmountRepresenter.getInstance());
            case MINOR_CURRENCY_UNIT:
                return new JodaMoneyModule(MinorCurrencyUnitAmountRepresenter.getInstance());
        }
        throw new IllegalArgumentException("Unrecognized amount representation: " + representation);
    }
}
