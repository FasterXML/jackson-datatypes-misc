# Jackson Datatype Money

*Jackson Datatype Money* is a [Jackson](https://github.com/codehaus/jackson) module to support JSON serialization and
deserialization of [JSR 354](https://github.com/JavaMoney/jsr354-api) data types. It fills a niche, in that it
integrates [MonetaryAmount](https://javamoney.github.io/apidocs/javax/money/MonetaryAmount.html) and Jackson so that they work seamlessly together, without requiring additional
developer effort. In doing so, it aims to perform a small but repetitive task — once and for all.

This library reflects an opinionated API [representation of monetary amounts in JSON](MONEY.md)

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

## Installation

Add the following dependency to your project:

```xml

<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-money</artifactId>
    <version>${jackson-datatype-money.version}</version>
</dependency>
```

For ultimate flexibility, this module is compatible with any implementation of JSR 354 MonetaryAmount

## Configuration

Register the module with your `ObjectMapper`:

```java
ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaxMoneyModule())
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
                .addModule(new JavaxMoneyModule().withQuotedDecimalNumbers())
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
                .addModule(new JavaxMoneyModule().withDefaultFormatting())
                .build();
```

... or pass in a `MonetaryAmountFormatFactory` implementation to the `JavaxMoneyModule`:

```java
ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaxMoneyModule()
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

This module will not have a default deserialization feature. 
In order to deserialize money values, one has to configure the module to use a specific implementation of `javax.money.MonetaryAmount`.
This can be done by passing the required `MonetaryAmountFactory` to the `JavaxMoneyModule` along with the implementing class:

```java
ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaxMoneyModule()
                .withMonetaryAmountFactory(implementationClass, new CustomMonetaryAmountFactory()))
                .build();
```

You can also pass in a method reference:

```java
ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaxMoneyModule()
                .withMonetaryAmountFactory(FastMoney.class, FastMoney::of))
                .build();
```

Please note that, for Moneta implementations like Money, FastMoney and RoundedMoney, the sibling module `jackson-datatype-moneta` can be used.
Refer to [javamoney-moneta](../moneta/Readme.md) for more information.

Module supports deserialization of amount number from JSON number as well as from JSON string without any special
configuration required.

### Custom Field Names

As you have seen in the previous examples the `JavaxMoneyModule` uses the field names `amount`, `currency` and `formatted`
by default. Those names can be overridden if desired:

```java
ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaxMoneyModule()
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