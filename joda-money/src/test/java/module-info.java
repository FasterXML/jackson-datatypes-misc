// Joda-Money module (unit) Test Module descriptor
module tools.jackson.datatype.joda
{
    // Since we are not split from Main artifact, will not
    // need to depend on Main artifact -- but need its dependencies

    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires tools.jackson.databind;

    requires org.joda.money;

    // Additional test lib/framework dependencies
    requires junit; // JUnit 4

    // Further, need to open up test packages for JUnit et al
    opens tools.jackson.datatype.jodamoney;
}
