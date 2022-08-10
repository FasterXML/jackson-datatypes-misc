// Copied+modified from jakarta-jsonp one on 20-Aug-2021
module tools.jackson.datatype.jakarta.mail {
    requires tools.jackson.core;
    requires tools.jackson.databind;

    requires jakarta.mail;

    exports tools.jackson.datatype.jakarta.mail;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.jakarta.mail.JakartaMailModule;
}
