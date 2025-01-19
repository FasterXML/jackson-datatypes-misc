// Jakarta JSONP module Main artifact Module descriptor
module tools.jackson.datatype.jsonp
{
    requires tools.jackson.core;
    requires transitive tools.jackson.databind;

    requires jakarta.json;

    exports tools.jackson.datatype.jsonp;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.jsonp.JSONPModule;
}
