package tools.jackson.datatype.moneta;

import org.junit.jupiter.api.Test;

import tools.jackson.datatype.javax.money.JavaxMoneyModule;

import static org.junit.jupiter.api.Assertions.*;

public class ModuleNameTest
{
    @Test
    public void testModuleName() {
        assertEquals("MonetaMoneyModule", new MonetaMoneyModule().getModuleName());
    }

    @Test
    public void testNameDiffersFromBaseModule() {
        assertNotEquals(new JavaxMoneyModule().getModuleName(),
                new MonetaMoneyModule().getModuleName());
    }

    // Base module keeps its own (correct) name
    @Test
    public void testBaseModuleName() {
        assertEquals("JavaxMoneyModule", new JavaxMoneyModule().getModuleName());
    }
}
