// Moneta module (unit) Test Module descriptor
module tools.jackson.datatype.moneta
{
    // Since we are not split from Main artifact, will not
    // need to depend on Main artifact -- but need its dependencies

    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires tools.jackson.databind;

    requires tools.jackson.datatype.javax.money;
    requires java.money;
    requires org.javamoney.moneta;

    // compile-time only dependencies
    requires static org.apiguardian.api;

    // Additional test lib/framework dependencies
    requires org.assertj.core;
    requires org.mockito;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;

    // Further, need to open up test packages for JUnit et al
    opens tools.jackson.datatype.moneta;
}
