package com.fasterxml.jackson.datatype.jodamoney;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import com.fasterxml.jackson.core.Version;

import com.fasterxml.jackson.databind.JacksonModule;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;

public class JodaMoneyModule extends JacksonModule
    implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    public JodaMoneyModule() { }

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
        desers.addDeserializer(Money.class, new MoneyDeserializer());
        context.addDeserializers(desers);

        final SimpleSerializers sers = new SimpleSerializers();
        sers.addSerializer(CurrencyUnit.class, new CurrencyUnitSerializer());
        sers.addSerializer(Money.class, new MoneySerializer());
        context.addSerializers(sers);
    }
}
