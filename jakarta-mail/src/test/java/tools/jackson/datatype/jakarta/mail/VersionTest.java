package tools.jackson.datatype.jakarta.mail;

import org.junit.jupiter.api.Test;

import tools.jackson.core.Version;
import tools.jackson.core.Versioned;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VersionTest
{
    @Test
    public void testModuleVersionAndName() throws Exception {
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

