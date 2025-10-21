// Jakarta Mail module Main artifact Module descriptor
module tools.jackson.datatype.jakarta_mail
{
    requires tools.jackson.core;
    requires transitive tools.jackson.databind;

    requires jakarta.mail;

    exports tools.jackson.datatype.jakarta.mail;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.jakarta.mail.JakartaMailModule;
}
