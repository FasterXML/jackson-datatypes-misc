// Copied+modified from jakarta-jsonp one on 20-Aug-2021
module com.fasterxml.jackson.datatype.jakarta.mail {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    requires jakarta.mail;

    exports com.fasterxml.jackson.datatype.jakarta.mail;

    provides com.fasterxml.jackson.databind.Module with
        com.fasterxml.jackson.datatype.jakarta.mail.JakartaMailModule;
}
