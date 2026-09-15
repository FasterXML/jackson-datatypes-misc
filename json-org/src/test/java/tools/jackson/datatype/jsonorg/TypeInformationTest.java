package tools.jackson.datatype.jsonorg;

import org.junit.jupiter.api.Test;

import org.json.*;

import tools.jackson.databind.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to verify that we can also use JSONObject and JSONArray
 * with polymorphic type information.
 */
public class TypeInformationTest extends ModuleTestBase
{
    static class ObjectWrapper {
        public Object value;

        public ObjectWrapper(Object v) { value = v; }
        public ObjectWrapper() { }
    }

    private final ObjectMapper POLY_MAPPER = newMapperBuilder()
            .activateDefaultTyping(new NoCheckSubTypeValidator())
            .build();

    private final ObjectMapper POLY_PROP_MAPPER = newMapperBuilder()
            .activateDefaultTypingAsProperty(new NoCheckSubTypeValidator(),
                    DefaultTyping.NON_FINAL, "@class")
            .build();

    @Test
    public void testWrappedArray() throws Exception
    {
        JSONTokener tok = new JSONTokener("[13]");
        JSONArray array = (JSONArray) tok.nextValue();

        String json = POLY_MAPPER.writeValueAsString(new ObjectWrapper(array));
        assertEquals("{\"value\":[\"org.json.JSONArray\",[13]]}", json);

        ObjectWrapper result = POLY_MAPPER.readValue(json, ObjectWrapper.class);
        assertEquals(JSONArray.class, result.value.getClass());
        JSONArray resultArray = (JSONArray) result.value;
        assertEquals(1, resultArray.length());
        assertEquals(13, resultArray.getInt(0));
    }

    @Test
    public void testWrappedObject() throws Exception
    {
        JSONTokener tok = new JSONTokener("{\"a\":true}");
        JSONObject array = (JSONObject) tok.nextValue();

        String json = POLY_MAPPER.writeValueAsString(new ObjectWrapper(array));
        assertEquals("{\"value\":[\"org.json.JSONObject\",{\"a\":true}]}", json);

        ObjectWrapper result = POLY_MAPPER.readValue(json, ObjectWrapper.class);
        assertEquals(JSONObject.class, result.value.getClass());
        JSONObject resultOb = (JSONObject) result.value;
        assertEquals(1, resultOb.length());
        assertTrue(resultOb.getBoolean("a"));
    }

    // [datatypes-misc#90]: with As-Property Type Id, deserializer is called with
    // parser positioned past the Type Id: PROPERTY_NAME, or END_OBJECT for empty Object
    @Test
    public void testObjectWithTypeIdAsProperty() throws Exception
    {
        JSONObject ob = new JSONObject();
        ob.put("a", true);

        String json = POLY_PROP_MAPPER.writeValueAsString(new ObjectWrapper(ob));
        assertEquals(a2q("{'@class':'"+ObjectWrapper.class.getName()+"',"
                +"'value':{'@class':'org.json.JSONObject','a':true}}"), json);

        ObjectWrapper result = POLY_PROP_MAPPER.readValue(json, ObjectWrapper.class);
        assertEquals(JSONObject.class, result.value.getClass());
        JSONObject resultOb = (JSONObject) result.value;
        assertEquals(1, resultOb.length());
        assertTrue(resultOb.getBoolean("a"));
    }

    @Test
    public void testEmptyObjectWithTypeIdAsProperty() throws Exception
    {
        String json = POLY_PROP_MAPPER.writeValueAsString(new ObjectWrapper(new JSONObject()));
        assertEquals(a2q("{'@class':'"+ObjectWrapper.class.getName()+"',"
                +"'value':{'@class':'org.json.JSONObject'}}"), json);

        ObjectWrapper result = POLY_PROP_MAPPER.readValue(json, ObjectWrapper.class);
        assertEquals(JSONObject.class, result.value.getClass());
        assertEquals(0, ((JSONObject) result.value).length());
    }
}
