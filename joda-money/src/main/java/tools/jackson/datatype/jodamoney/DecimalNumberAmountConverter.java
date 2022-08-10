package tools.jackson.datatype.jodamoney;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * An {@link AmountConverter} converting {@link Money} to its amount represented as
 * {@link BigDecimal decimal number} (such as {@code 12.34} for {@code Money.parse("USD 12.34")}),
 * and back to {@code Money} from this representation.
 */
final class DecimalNumberAmountConverter implements AmountConverter {

    private static final DecimalNumberAmountConverter INSTANCE = new DecimalNumberAmountConverter();

    static DecimalNumberAmountConverter getInstance() {
        return INSTANCE;
    }

    private DecimalNumberAmountConverter() {
    }

    @Override
    public BigDecimal fromMoney(final Money money) {
        final BigDecimal decimal = money.getAmount();
        final int decimalPlaces = money.getCurrencyUnit().getDecimalPlaces();
        final int scale = Math.max(decimal.scale(), decimalPlaces);
        return decimal.setScale(scale, RoundingMode.UNNECESSARY);
    }

    @Override
    public Money toMoney(final CurrencyUnit currencyUnit, final BigDecimal amount) {
        return Money.of(currencyUnit, amount);
    }
}
