// Hand-crafted 05-Nov-2024
module tools.jackson.datatype.javax.money
{
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires tools.jackson.databind;
    requires javax.money;

    exports tools.jackson.datatype.javax.money;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.javax.money.JavaxMoneyModule;
}
