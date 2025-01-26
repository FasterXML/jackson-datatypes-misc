package tools.jackson.datatype.jodamoney;

import java.util.Arrays;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

public abstract class ModuleTestBase
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
