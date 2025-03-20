package tools.jackson.datatype.jodamoney;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * An {@code AmountConverter} converting {@link Money} to its amount represented in
 * {@link Money#getAmountMinorLong() minor units as a long} (such as {@code 1234} for
 * {@code Money.parse("USD 12.34")}), and back to {@code Money} from this representation.
 */
final class MinorCurrencyUnitAmountConverter implements AmountConverter
{
    private static final MinorCurrencyUnitAmountConverter INSTANCE = new MinorCurrencyUnitAmountConverter();

    static MinorCurrencyUnitAmountConverter getInstance() {
        return INSTANCE;
    }

    private MinorCurrencyUnitAmountConverter() {
    }

    @Override
    public Long fromMoney(final Money money) {
        return money.getAmountMinorLong();
    }

    @Override
    public Money toMoney(final CurrencyUnit currencyUnit, final BigDecimal amount) {
        return Money.of(currencyUnit, amount.movePointLeft(currencyUnit.getDecimalPlaces()), RoundingMode.UNNECESSARY);
    }
}
