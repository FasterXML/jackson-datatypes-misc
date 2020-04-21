// Generated 28-Mar-2019 using Moditect maven plugin
module com.fasterxml.jackson.datatype.jsr353 {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    requires javax.json.api;

    exports com.fasterxml.jackson.datatype.jsr353;

    provides com.fasterxml.jackson.databind.Module with
        com.fasterxml.jackson.datatype.jsr353.JSR353Module;
}
