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
    requires static lombok;

    // Additional test lib/framework dependencies
    requires org.junit.jupiter.api;

    requires org.javamoney.moneta;

    // Further, need to open up test packages for JUnit et al
    opens tools.jackson.datatype.javax.money;
}
