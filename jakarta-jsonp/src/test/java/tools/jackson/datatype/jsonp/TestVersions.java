package tools.jackson.datatype.jsonp;

import java.io.*;

import tools.jackson.core.Version;
import tools.jackson.core.Versioned;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestVersions extends TestBase
{
    @Test
    public void testModuleVersionAndName() throws IOException
    {
        JSONPModule module = new JSONPModule();
        assertVersion(module);
        // just because name is assigned programmatically
        assertEquals("jackson-datatype-jakarta-jsonp",
                module.getModuleName());
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */
    
    private void assertVersion(Versioned vers)
    {
        final Version v = vers.version();
        assertEquals(PackageVersion.VERSION, v);
    }
}

