package tools.jackson.datatype.jakarta.mail;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import tools.jackson.core.Version;
import tools.jackson.core.Versioned;

import org.junit.Test;

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

