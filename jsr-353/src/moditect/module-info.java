// Generated 28-Mar-2019 using Moditect maven plugin
module com.fasterxml.jackson.datatype.jsr353 {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    // 08-Jan-2020, tatu: changed in 2.12.1 from "javax.json.api"
    requires java.json;

    exports com.fasterxml.jackson.datatype.jsr353;

    provides com.fasterxml.jackson.databind.JacksonModule with
        com.fasterxml.jackson.datatype.jsr353.JSR353Module;
}
