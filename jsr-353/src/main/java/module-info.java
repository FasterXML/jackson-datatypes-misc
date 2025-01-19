// JSR-353 module Main artifact Module descriptor
module tools.jackson.datatype.jsr353
{
    requires tools.jackson.core;
    requires transitive tools.jackson.databind;

    // 08-Jan-2020, tatu: changed in 2.12.1 from "javax.json.api"
    requires java.json;

    exports tools.jackson.datatype.jsr353;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.jsr353.JSR353Module;
}
