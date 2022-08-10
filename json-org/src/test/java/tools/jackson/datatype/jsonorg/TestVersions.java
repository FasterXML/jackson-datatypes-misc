package tools.jackson.datatype.jsonorg;

import java.io.*;

import tools.jackson.core.Version;
import tools.jackson.core.Versioned;

public class TestVersions extends ModuleTestBase
{
    public void testModuleVersionAndName() throws IOException
    {
        JsonOrgModule module = new JsonOrgModule();
        assertVersion(module);
        // just because name is assigned programmatically
        assertEquals("jackson-datatype-json-org",
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

