module tools.jackson.datatype.jsonorg {
    requires tools.jackson.core;
    requires tools.jackson.databind;

    // is this the package name
    requires static json;
    //^2015
    requires static org.json;

    exports tools.jackson.datatype.jsonorg;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.jsonorg.JsonOrgModule;
}
