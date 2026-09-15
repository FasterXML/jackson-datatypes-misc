package tools.jackson.datatype.jsonorg;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.json.JSONObject;

import tools.jackson.core.type.TypeReference;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to verify that deserialization of {@link JSONObject} fails cleanly for
 * non-Object input, instead of quietly returning an empty {@code JSONObject}
 * (and, for structured input, leaving the parser mid-value).
 */
public class FailOnNonObjectTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();

    @Test
    public void testFailOnNumber() throws Exception
    {
        try {
            JSONObject ob = MAPPER.readValue("42", JSONObject.class);
            fail("Should not pass but got: "+ob);
        } catch (MismatchedInputException e) {
            verifyException(e, "Unexpected token (VALUE_NUMBER_INT), expected START_OBJECT");
        }
    }

    @Test
    public void testFailOnString() throws Exception
    {
        try {
            JSONObject ob = MAPPER.readValue(a2q("'abc'"), JSONObject.class);
            fail("Should not pass but got: "+ob);
        } catch (MismatchedInputException e) {
            verifyException(e, "Unexpected token (VALUE_STRING), expected START_OBJECT");
        }
    }

    @Test
    public void testFailOnArray() throws Exception
    {
        try {
            JSONObject ob = MAPPER.readValue("[1,2]", JSONObject.class);
            fail("Should not pass but got: "+ob);
        } catch (MismatchedInputException e) {
            verifyException(e, "Unexpected token (START_ARRAY), expected START_OBJECT");
        }
    }

    // Most importantly: bad element must not desync the parser for the ones that follow
    @Test
    public void testFailOnScalarWithinList() throws Exception
    {
        try {
            List<JSONObject> obs = MAPPER.readValue(a2q("[{'a':1},42,{'b':2}]"),
                    new TypeReference<List<JSONObject>>() { });
            fail("Should not pass but got: "+obs);
        } catch (MismatchedInputException e) {
            verifyException(e, "Unexpected token (VALUE_NUMBER_INT), expected START_OBJECT");
        }
    }

    @Test
    public void testFailOnArrayWithinList() throws Exception
    {
        try {
            List<JSONObject> obs = MAPPER.readValue(a2q("[{'a':1},[7,8],{'b':2}]"),
                    new TypeReference<List<JSONObject>>() { });
            fail("Should not pass but got: "+obs);
        } catch (MismatchedInputException e) {
            verifyException(e, "Unexpected token (START_ARRAY), expected START_OBJECT");
        }
    }

    // But valid Objects must keep working, including empty ones
    @Test
    public void testEmptyObjectStillOk() throws Exception
    {
        assertEquals(0, MAPPER.readValue("{}", JSONObject.class).length());
    }

    @Test
    public void testObjectListStillOk() throws Exception
    {
        List<JSONObject> obs = MAPPER.readValue(a2q("[{'a':1},{'b':2}]"),
                new TypeReference<List<JSONObject>>() { });
        assertEquals(2, obs.size());
        assertEquals(1, obs.get(0).getInt("a"));
        assertEquals(2, obs.get(1).getInt("b"));
    }
}
