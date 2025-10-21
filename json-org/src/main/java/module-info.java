// Org.json module Main artifact Module descriptor
module tools.jackson.datatype.jsonorg
{
    requires tools.jackson.core;
    requires transitive tools.jackson.databind;

    requires org.json;

    exports tools.jackson.datatype.jsonorg;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.jsonorg.JsonOrgModule;
}
