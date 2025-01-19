// Jakarta JSONP module (unit) Test Module descriptor
module tools.jackson.datatype.jsonp
{
    // Since we are not split from Main artifact, will not
    // need to depend on Main artifact -- but need its dependencies

    requires tools.jackson.core;
    requires tools.jackson.databind;

    requires jakarta.json;

    // // Actual Test dependencies
    requires java.desktop; // @ConstructorProperties    

    // Additional test lib/framework dependencies
    requires junit; // JUnit 4

    // Further, need to open up test packages for JUnit et al
    opens tools.jackson.datatype.jsonp;
}
