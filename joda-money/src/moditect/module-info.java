module tools.jackson.datatype.joda {
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires tools.jackson.databind;
    requires joda.money;

    exports tools.jackson.datatype.jodamoney;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.jodamoney.JodaMoneyModule;
}
