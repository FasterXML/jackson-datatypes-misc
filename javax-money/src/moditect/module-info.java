//TODO how is this generated
// Generated 27-Mar-2019 using Moditect maven plugin
module com.fasterxml.jackson.datatype.money {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires javax.money;

    exports com.fasterxml.jackson.datatype.money;

    provides com.fasterxml.jackson.databind.Module with
        com.fasterxml.jackson.datatype.money.MoneyModule;
}
