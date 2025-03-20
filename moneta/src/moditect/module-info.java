// Hand-crafted 29-Jan-2025
module tools.jackson.datatype.moneta
{
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires tools.jackson.databind;

    requires tools.jackson.datatype.javax.money;

    requires java.money;

    exports tools.jackson.datatype.moneta;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.moneta.MonetaMoneyModule;
}
