// Moneta module Main artifact Module descriptor
module tools.jackson.datatype.moneta
{
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires tools.jackson.databind;

    requires tools.jackson.datatype.javax.money;
    requires java.money;
    requires org.javamoney.moneta;

    // compile-time only dependencies
    requires static org.apiguardian.api;

    
    exports tools.jackson.datatype.moneta;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.moneta.MonetaMoneyModule;
}
