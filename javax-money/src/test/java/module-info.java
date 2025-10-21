// Javax-money module (unit) Test Module descriptor
module tools.jackson.datatype.javax.money
{
    // Since we are not split from Main artifact, will not
    // need to depend on Main artifact -- but need its dependencies

    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires tools.jackson.databind;

    requires java.money;

    // compile-time only dependencies
    requires static org.apiguardian.api;

    // Additional test lib/framework dependencies
    requires org.assertj.core;
    requires org.mockito;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;

    requires org.javamoney.moneta;

    // Further, need to open up test packages for JUnit et al
    opens tools.jackson.datatype.javax.money;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.javax.money.JavaxMoneyModule;
    uses tools.jackson.databind.JacksonModule;
}
