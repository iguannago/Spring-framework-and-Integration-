package common.money;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Unit tests for {@link MonetaryAmountFormatter}
 * <p/>
 * User: djnorth
 * Date: 15/11/2012
 * Time: 13:01
 */
public class MonetaryAmountFormatterTests extends MonetaryAmountTestBase {


    /**
     * Formatter to test
     */
    private final MonetaryAmountFormatter monetaryAmountFormatter = new MonetaryAmountFormatter();

    /**
     * Constructor taking test arguments
     *
     * @param inputString
     * @param validInput
     * @param constructionValueBigDecimal
     * @param expectedValueBigDecimal
     * @param expectedOutputString
     */
    public MonetaryAmountFormatterTests(String inputString, boolean validInput, BigDecimal constructionValueBigDecimal, BigDecimal expectedValueBigDecimal,
                                        String expectedOutputString) {
        super(inputString, validInput, constructionValueBigDecimal, expectedValueBigDecimal, expectedOutputString);
    }


    /**
     * Test parsing
     */
    @Test
    public void testParse() {
        try {
            MonetaryAmount actualAmount = monetaryAmountFormatter.parse(getInputString(), Locale.getDefault());
            Assert.assertTrue("should throw exception for invalid input:" + this, isValidInput());
            Assert.assertEquals("expectedValue:" + this, getExpectedValue(), actualAmount);
        } catch (NumberFormatException nfe) {
            Assert.assertFalse("should not throw exception=" + nfe + " for valid input:" + this, isValidInput());
        }
    }


    /**
     * Test printing
     */
    @Test
    public void testPrint() {
        String actualOutputString = monetaryAmountFormatter.print(getExpectedValue(), Locale.getDefault());
        Assert.assertEquals("outputString:" + this, getExpectedOutputString(), actualOutputString);
    }


    /**
     * @return readable representation of our state
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(super.toString());
        sb.append("{monetaryAmountFormatter=").append(monetaryAmountFormatter);
        sb.append('}');
        return sb.toString();
    }
}
