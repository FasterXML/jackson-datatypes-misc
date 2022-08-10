package com.fasterxml.jackson.datatype.jsonorg;

import tools.jackson.databind.module.SimpleModule;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonOrgModule extends SimpleModule
{
    private static final long serialVersionUID = 1;

    /*
    /**********************************************************************
    /* Life-cycle
    /**********************************************************************
     */

    public JsonOrgModule()
    {
        super(PackageVersion.VERSION);
        addDeserializer(JSONArray.class, JSONArrayDeserializer.instance);
        addDeserializer(JSONObject.class, JSONObjectDeserializer.instance);
        addSerializer(JSONArraySerializer.instance);
        addSerializer(JSONObjectSerializer.instance);
    }
}
