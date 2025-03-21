// Javax-money module Main artifact Module descriptor
module tools.jackson.datatype.javax.money
{
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires tools.jackson.databind;

    requires java.money;

    // compile-time only dependencies
    requires static org.apiguardian.api;
    requires static lombok;
    
    exports tools.jackson.datatype.javax.money;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.javax.money.JavaxMoneyModule;
}
