package com.fasterxml.jackson.datatype.jsr353;

import java.io.*;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;

public class TestVersions extends TestBase
{
    public void testModuleVersionAndName() throws IOException
    {
        JSR353Module module = new JSR353Module();
        assertVersion(module);
        // just because name is assigned programmatically
        assertEquals("JSR353Module",
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

