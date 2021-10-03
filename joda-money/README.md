One of [Jackson](../../../..jackson) [Misec. datatype modules](../../..).
Supports JSON serialization and deserialization of
["org.json" JSON library](http://json.org/java) datatypes, most commonly
seen in Android SDK.

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-joda-money</artifactId>
  <version>2.11.0</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

### Registering module

Like all standard Jackson modules (libraries that implement Module interface), registration is done as follows (Jackson 2.x up to 2.9)

```java
// import com.fasterxml.jackson.datatype.jodamoney.JodaMoneyModule;

ObjectMapper mapper = new ObjectMapper()
    .registerModule(new JodaMoneyModule());
```
OR, the new method added in 2.10 (old method will work with 2.x but not 3.x):

```java
ObjectMapper mapper = JsonMapper.builder()
    .addModule(new JodaMoneyModule())
    .build();
```

after which functionality is available with all normal Jackson operations, like:

```java
Money amount = mapper.readValue("{\"currency\":\"EUR\",\"amount\":19.99}", Money.class)
assertEquals("EUR", amount.getCurrencyUnit().getCode())
assertEquals(BigDecimal.valueOf(19.99), amount.getAmount())
```

### Configuring the module

#### Amount representation

Representation of the amount for the (de)serialized `Money` instances can be configured via the `JodaMoneyModule.withAmountRepresentation(AmountRepresentation)` method. The available representations are:

* `DECIMAL_NUMBER` - the default; amounts are (de)serialized as decimal numbers equal to the monetary amount, e.g. `12.34` for EUR 12.34,
* `DECIMAL_STRING` - amounts are (de)serialized as strings containing decimal number equal to the monetary amount, e.g. `"12.34"` for EUR 12.34,
* `MINOR_CURRENCY_UNIT` - amounts are (de)serialized as long integers equal to the monetary amount expressed in minor currency unit, e.g. `1234` for EUR 12.34, `12345` for KWD 12.345 or `12` for JPY 12.
