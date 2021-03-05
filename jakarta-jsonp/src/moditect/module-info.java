// Copied+modified from jsr-353 ones on 04-Mar-2021
module com.fasterxml.jackson.datatype.jsonp {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    requires jakarta.json;

    exports com.fasterxml.jackson.datatype.jakarta.jsonp;

    provides com.fasterxml.jackson.databind.Module with
        com.fasterxml.jackson.datatype.jsonp.JSONPModule;
}
