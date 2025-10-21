package tools.jackson.datatype.jakarta.mail;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.exc.InvalidFormatException;

import jakarta.mail.internet.InternetAddress;

import static org.junit.jupiter.api.Assertions.*;

public class SerDesTests extends TestBase {

    private static final String ADDRESS = "alice.adams@example.test";
    private static final String PERSONAL = "Alice Adams";

    private static final String MAILBOX = PERSONAL + " <" + ADDRESS + ">";
    private static final String QUOTED_MAILBOX = '"' + MAILBOX + '"';

    @Test
    public void checkExpectedMailbox() throws UnsupportedEncodingException {
        InternetAddress addr = new InternetAddress(ADDRESS, PERSONAL);
        assertEquals(MAILBOX, addr.toString());
    }


    @Test
    public void serializeAddress() throws Exception {
        InternetAddress addr = new InternetAddress(ADDRESS, PERSONAL);

        String json = sharedMapper().writeValueAsString(addr);
        assertEquals(QUOTED_MAILBOX, json);
    }

    @Test
    public void deserializeAddress() throws Exception {
        InternetAddress deserialized = sharedMapper().readValue(QUOTED_MAILBOX, InternetAddress.class);
        assertEquals(new InternetAddress(ADDRESS, PERSONAL), deserialized);
    }

    @Test
    public void onlyAddress() throws Exception {
        InternetAddress onlyAddress = new InternetAddress(ADDRESS);
        String json = sharedMapper().writeValueAsString(onlyAddress);
        InternetAddress roundTripped = sharedMapper().readValue(json, InternetAddress.class);

        assertEquals(onlyAddress, roundTripped);
    }

    @Test
    public void invalidThrows() throws Exception {
        String json = "\"alice at example\"";
        assertThrows(InvalidFormatException.class,
                () -> sharedMapper().readValue(json, InternetAddress.class));
    }
}
