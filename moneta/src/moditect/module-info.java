// Hand-crafted 29-Jan-2025
module com.fasterxml.jackson.datatype.moneta {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires javax.money;

    exports com.fasterxml.jackson.datatype.moneta;

    provides com.fasterxml.jackson.databind.Module with
            com.fasterxml.jackson.datatype.moneta.MonetaMoneyModule;
}
