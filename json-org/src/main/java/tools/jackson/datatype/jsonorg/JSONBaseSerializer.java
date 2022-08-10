package tools.jackson.datatype.jsonorg;

import tools.jackson.databind.ser.std.StdSerializer;

abstract class JSONBaseSerializer<T> extends StdSerializer<T>
{
    protected JSONBaseSerializer(Class<T> cls) { super(cls); }
}
