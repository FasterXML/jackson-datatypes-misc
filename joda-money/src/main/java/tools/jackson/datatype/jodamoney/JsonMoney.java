package tools.jackson.datatype.jodamoney;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tools.jackson.annotation.JacksonAnnotation;

/**
 * Annotation for configuring serialization and deserialization of {@link org.joda.money.Money Money}
 * at the field level. This annotation allows per-property override of the amount representation
 * used when converting Money to/from JSON.
 * <p>
 * When applied to a field, getter, or constructor parameter, this annotation takes precedence over
 * the module-level configuration set via {@link JodaMoneyModule#withAmountRepresentation(AmountRepresentation)}.
 * <p>
 * Example usage:
 * <pre>
 * public class Payment {
 *     &#64;JsonMoney(amountRepresentation = AmountRepresentation.DECIMAL_STRING)
 *     private Money amount;
 *
 *     &#64;JsonMoney(amountRepresentation = AmountRepresentation.MINOR_CURRENCY_UNIT)
 *     private Money fee;
 * }
 * </pre>
 *
 * @see AmountRepresentation
 * @see JodaMoneyModule#withAmountRepresentation(AmountRepresentation)
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonMoney {

    /**
     * Specifies the amount representation to use for this property.
     * <p>
     * Defaults to {@link AmountRepresentation#DEFAULT}, which means the property
     * will use the module-level configuration or the built-in default
     * ({@link AmountRepresentation#DECIMAL_NUMBER}).
     *
     * @return the amount representation to use
     */
    AmountRepresentation amountRepresentation() default AmountRepresentation.DEFAULT;
}
