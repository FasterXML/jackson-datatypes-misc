package tools.jackson.datatype.jsr353;

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
        JSR353Module module = new JSR353Module();
        assertVersion(module);
        // just because name is assigned programmatically
        assertEquals("jackson-datatype-jsr353",
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

