package tools.jackson.datatype.jodamoney;

import java.io.*;

import tools.jackson.core.Versioned;

/**
 * Simple verification that version access works.
 */
public class TestVersions extends ModuleTestBase
{
    public void testVersions() throws IOException
    {
        assertVersion(new JodaMoneyModule());
    }

    // just because name is assigned programmatically
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

