package com.fasterxml.jackson.datatype.jakarta.mail;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VersionTest
{
    @Test
    public void testModuleVersionAndName() throws IOException {
        JakartaMailModule module = new JakartaMailModule();
        assertVersion(module);
        // just because name is assigned programmatically
        assertEquals("jackson-datatype-jakarta-mail",
                module.getModuleName());
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    private void assertVersion(Versioned vers) {
        final Version v = vers.version();
        assertEquals(PackageVersion.VERSION, v);
    }
}

