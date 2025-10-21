package tools.jackson.datatype.jodamoney;

import java.io.*;

import tools.jackson.core.Versioned;

/**
 * Simple verification that version access works.
 */
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestVersions extends ModuleTestBase
{
    @Test
    public void testVersions() throws IOException
    {
        assertVersion(new JodaMoneyModule());
    }

    // just because name is assigned programmatically
    @Test
    public void testModuleName()
    {
        assertEquals("jackson-datatype-joda-money",
                new JodaMoneyModule().getModuleName());
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    private void assertVersion(Versioned v)
    {
        assertEquals(PackageVersion.VERSION, v.version());
    }
}

