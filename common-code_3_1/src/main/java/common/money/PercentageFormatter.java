package common.money;

import org.springframework.format.Formatter;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Locale;

/**
 * Formatter for {@link Percentage} properties.
 * <p/>
 * Converts object values to well-formatted strings and strings back to values.
 * Usable by a data binding framework for binding user input to the model.
 */
public class PercentageFormatter implements Formatter<Percentage> {

    /**
     * Convert Percentage to string
     *
     * @param percentage
     * @param locale
     * @return resulting string
     */
    @Override
    public String print(Percentage percentage, Locale locale) {
        return (percentage == null ? null : percentage.toString());
    }


    /**
     * Convert string to percentage
     *
     * @param text
     * @param locale
     * @return percentage
     */
    @Override
    public Percentage parse(String text, Locale locale) {
        if (StringUtils.hasText(text)) {
            return Percentage.valueOf(text);
        } else {
            return null;
        }
    }

}
