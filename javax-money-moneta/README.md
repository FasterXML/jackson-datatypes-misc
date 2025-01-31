# Jackson Datatype Money

*Jackson Datatype Moneta* is a [Jackson](https://github.com/codehaus/jackson) module to support JSON serialization and
deserialization of [JavaMoney](https://github.com/JavaMoney/jsr354-api) data types with special support for JavaMoney's
Moneta datatypes.

With this library, it is possible to represent monetary amounts in JSON as follows:

```json
{
  "amount": 29.95,
  "currency": "EUR"
}
```

## Features

- enables you to express monetary amounts in JSON
- can be used in a REST APIs
- customized field names
- localization of formatted monetary amounts
- allows you to implement RESTful API endpoints that format monetary amounts based on the Accept-Language header
- is unique and flexible

## Dependencies

- Java 8 or higher
- Any build tool using Maven Central, or direct download
- Jackson
- JavaMoney

## Installation

Add the following dependency to your project:

```xml

<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-moneta</artifactId>
    <version>${jackson-datatype-moneta.version}</version>
</dependency>
```

For ultimate flexibility, this module is compatible with the official version as well as the backport of JavaMoney. The
actual version will be selected by a profile based on the current JDK version.

## Configuration

Register the module with your `ObjectMapper`:

```java
ObjectMapper mapper = JsonMapper.builder()
        .addModule(new MonetaMoneyModule())
        .build();
```

Alternatively, you can use the SPI capabilities:

```java
ObjectMapper mapper = new ObjectMapper()
        .findAndRegisterModules();
```

### Serialization

For serialization this module currently supports
[
`javax.money.MonetaryAmount`](https://github.com/JavaMoney/jsr354-api/blob/master/src/main/java/javax/money/MonetaryAmount.java)
and will, by default, serialize it as:

```json
{
  "amount": 99.95,
  "currency": "EUR"
}
```

To serialize number as a JSON string, you have to configure the quoted decimal number value serializer:

```java
ObjectMapper mapper = JsonMapper.builder()
        .addModule(new MonetaMoneyModule().withQuotedDecimalNumbers())
        .build();
```

```json
{
  "amount": "99.95",
  "currency": "EUR"
}
```

### Formatting

A special feature for serializing monetary amounts is *formatting*, which is **disabled by default**. To enable it, you
have to either enable default formatting:

```java
ObjectMapper mapper = JsonMapper.builder()
        .addModule(new MonetaMoneyModule().withDefaultFormatting())
        .build();
```

... or pass in a `MonetaryAmountFormatFactory` implementation to the `MonetaMoneyModule`:

```java
ObjectMapper mapper = JsonMapper.builder()
        .addModule(new MonetaMoneyModule()
                .withFormatting(new CustomMonetaryAmountFormatFactory()))
        .build();
```

The default formatting delegates directly to `MonetaryFormats.getAmountFormat(Locale, String...)`.

Formatting only affects the serialization and can be customized based on the *current* locale, as defined by the
[
`SerializationConfig`](https://fasterxml.github.io/jackson-databind/javadoc/2.0.0/com/fasterxml/jackson/databind/SerializationConfig.html#with\(java.util.Locale\)).
This allows to implement RESTful API endpoints
that format monetary amounts based on the `Accept-Language` header.

The first example serializes a monetary amount using the `de_DE` locale:

```java
ObjectWriter writer = mapper.writer().with(Locale.GERMANY);
writer.writeValueAsString(Money.of(29.95, "EUR"));
```

```json
{
  "amount": 29.95,
  "currency": "EUR",
  "formatted": "29,95 EUR"
}
```

The following example uses `en_US`:

```java
ObjectWriter writer = mapper.writer().with(Locale.US);
writer.writeValueAsString(Money.of(29.95, "USD"));
```

```json
{
  "amount": 29.95,
  "currency": "USD",
  "formatted": "USD29.95"
}
```

More sophisticated formatting rules can be supported by implementing `MonetaryAmountFormatFactory` directly.

### Deserialization

This module will use `org.javamoney.moneta.Money` as an implementation for `javax.money.MonetaryAmount` by default when
deserializing money values.

In addition, this module comes with support for all `MonetaryAmount` implementations from Moneta, the reference
implementation of JavaMoney:

| `MonetaryAmount` Implementation     | Factory                                      |
|-------------------------------------|----------------------------------------------|
| `org.javamoney.moneta.FastMoney`    | `new MonetaMoneyModule().withFastMoney()`    |
| `org.javamoney.moneta.Money`        | `new MonetaMoneyModule().withMoney()`        |
| `org.javamoney.moneta.RoundedMoney` | `new MonetaMoneyModule().withRoundedMoney()` |                                                                                                                             |

Module supports deserialization of amount number from JSON number as well as from JSON string without any special
configuration required.

### Custom Field Names

As you have seen in the previous examples the `MonetaMoneyModule` uses the field names `amount`, `currency` and
`formatted`
by default. Those names can be overridden if desired:

```java
ObjectMapper mapper = JsonMapper.builder()
        .addModule(new MonetaMoneyModule()
                .withAmountFieldName("value")
                .withCurrencyFieldName("unit")
                .withFormattedFieldName("pretty"))
        .build();
```

## Usage

After registering and configuring the module you're now free to directly use `MonetaryAmount` in your data types:

```java
import javax.money.MonetaryAmount;

public class Product {
    private String sku;
    private MonetaryAmount price;
    ...
}
```