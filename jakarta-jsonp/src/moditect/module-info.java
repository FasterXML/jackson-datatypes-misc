// Generated 28-Mar-2019 using Moditect maven plugin
module com.fasterxml.jackson.datatype.jakarta.jsonp {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    requires jakarta.json;

    exports com.fasterxml.jackson.datatype.jakarta.jsonp;

    provides com.fasterxml.jackson.databind.Module with
        com.fasterxml.jackson.datatype.jakarta.jsonp.JSONPModule;
}
