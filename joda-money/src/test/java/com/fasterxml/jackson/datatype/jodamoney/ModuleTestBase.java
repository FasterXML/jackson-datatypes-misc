package com.fasterxml.jackson.datatype.jodamoney;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.json.JsonMapper;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.function.Function;

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

    protected static ObjectMapper mapperWithModule() {
        return mapperWithModuleBuilder().build();
    }

    protected static ObjectMapper mapperWithModule(Function<JodaMoneyModule, JodaMoneyModule> customizations) {
        return JsonMapper.builder()
            .addModule(customizations.apply(new JodaMoneyModule()))
            .build();
    }

    /*
    /**********************************************************************
    /* Additional assert methods
    /**********************************************************************
     */

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
            if (lmsg.contains(lmatch)) {
                return;
            }
        }
        fail("Expected an exception (type "+e.getClass().getName()+") with one of substrings ("
+Arrays.asList(matches)+"): got one with message \""+msg+"\"");
    }

    protected String json(String format, Object... args) {
        return String.format(format, args).replace('\'', '"');
    }
}
