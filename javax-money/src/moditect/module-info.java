// Hand-crafted 05-Nov-2024
module com.fasterxml.jackson.datatype.money {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires javax.money;

    exports com.fasterxml.jackson.datatype.money;

    provides com.fasterxml.jackson.databind.Module with
        com.fasterxml.jackson.datatype.money.JavaxMoneyModule;
}
