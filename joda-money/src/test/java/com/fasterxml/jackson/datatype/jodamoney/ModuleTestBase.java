package com.fasterxml.jackson.datatype.jodamoney;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.json.JsonMapper;

import junit.framework.TestCase;

import static org.junit.Assert.*;

public abstract class ModuleTestBase extends TestCase
{
    // mix-in class for force polymorphic handling
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    static interface ForceJsonTypeInfo { }

    /*
    /**********************************************************************
    /* Factory methods
    /**********************************************************************
     */

    protected static MapperBuilder<?,?> mapperWithModuleBuilder() {
        return JsonMapper.builder()
                .addModule(new JodaMoneyModule());
    }

    protected static MapperBuilder<?,?> jodaMapperBuilder(DateFormat df) {
        return mapperWithModuleBuilder()
                .defaultDateFormat(df);
    }
    
    protected static MapperBuilder<?,?> jodaMapperBuilder(TimeZone tz) {
        return mapperWithModuleBuilder()
                .defaultTimeZone(tz);
    }

    protected static ObjectMapper mapperWithModule() {
        return mapperWithModuleBuilder().build();
    }

    protected static ObjectMapper mapperWithModule(DateFormat df) {
        return jodaMapperBuilder(df)
                .build();
    }

    protected static ObjectMapper mapperWithModule(TimeZone tz) {
        return jodaMapperBuilder(tz)
                .build();
    }

    /*
    /**********************************************************************
    /* Additional assert methods
    /**********************************************************************
     */

    protected void assertEquals(int[] exp, int[] act) {
        assertArrayEquals(exp, act);
    }
    
    /*
    /**********************************************************************
    /* Helper methods
    /**********************************************************************
     */

    protected void verifyException(Throwable e, String... matches)
    {
        String msg = e.getMessage();
        String lmsg = (msg == null) ? "" : msg.toLowerCase();
        for (String match : matches) {
            String lmatch = match.toLowerCase();
            if (lmsg.indexOf(lmatch) >= 0) {
                return;
            }
        }
        fail("Expected an exception (type "+e.getClass().getName()+") with one of substrings ("
+Arrays.asList(matches)+"): got one with message \""+msg+"\"");
    }

    public String q(String str) {
        return '"'+str+'"';
    }

    protected String a2q(String json) {
        return json.replace("'", "\"");
    }
}
