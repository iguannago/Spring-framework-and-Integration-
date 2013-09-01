package common.money;

import org.springframework.format.Formatter;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Locale;

/**
 * Formatter for {@link MonetaryAmount}
 * <p/>
 * To keep things simple, we're just going to assume dollars. A more realistic Monteary amount class would include a
 * currency code.
 */
public class MonetaryAmountFormatter implements Formatter<MonetaryAmount> {

    /**
     * Convert the given MonetaryAmount to a String
     *
     * @param amount The amount in question
     * @param locale The currently active locale
     * @return The converted amount
     */
    public String print(MonetaryAmount amount, Locale locale) {
        return (amount == null ? null : amount.toString());
    }

    /**
     * Converts the given string to a monetary amount
     *
     * @param text   The string to convert
     * @param locale The currently active locale
     * @return The converted amount
     */
    public MonetaryAmount parse(String text, Locale locale) {
        if (StringUtils.hasText(text)) {
            return MonetaryAmount.valueOf(text);
        } else {
            return null;
        }
    }
}
