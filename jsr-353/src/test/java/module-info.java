// JSR-353 module (unit) Test Module descriptor
module tools.jackson.datatype.jsr353
{
    // Since we are not split from Main artifact, will not
    // need to depend on Main artifact -- but need its dependencies

    requires tools.jackson.core;
    requires tools.jackson.databind;

    requires java.json;

    // Additional test dependencies
    requires java.desktop; // @ConstructorProperties

    // Additional test lib/framework dependencies
    requires org.assertj.core;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;

    // Further, need to open up test packages for JUnit et al
    opens tools.jackson.datatype.jsr353;
}
