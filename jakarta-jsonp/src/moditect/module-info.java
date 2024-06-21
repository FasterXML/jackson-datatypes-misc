// Copied+modified from jsr-353 ones on 04-Mar-2021
module tools.jackson.datatype.jsonp {
    requires tools.jackson.core;
    requires tools.jackson.databind;

    requires jakarta.json;

    exports tools.jackson.datatype.jsonp;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.jsonp.JSONPModule;
}
