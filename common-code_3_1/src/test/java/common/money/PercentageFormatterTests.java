package common.money;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Unit tests for {@link PercentageFormatter}
 * <p/>
 * User: djnorth
 * Date: 15/11/2012
 * Time: 17:29
 */
public class PercentageFormatterTests extends PercentageTestBase {

    /**
     * Percentage formatter to test
     */
    private PercentageFormatter percentageFormatter = new PercentageFormatter();

    /**
     * Constructor setting data fields
     *
     * @param inputString
     * @param validity
     * @param constructionValueBigDecimal
     * @param expectedValueBigDecimal
     * @param expectedOutputString
     */
    public PercentageFormatterTests(String inputString, Validity validity,
                                    BigDecimal constructionValueBigDecimal, BigDecimal expectedValueBigDecimal,
                                    String expectedOutputString) {
        super(inputString, validity, constructionValueBigDecimal, expectedValueBigDecimal, expectedOutputString);
    }


    /**
     * Test parsing
     */
    @Test
    public void testParse() {
        try {
            Percentage actualPercentage = percentageFormatter.parse(getInputString(), Locale.getDefault());
            if (StringUtils.hasText(getInputString())) {
                Assert.assertEquals("should throw exception for invalid input:" + this, Validity.VALID, getValidity());
            }
            Assert.assertEquals("expectedValue:" + this, getExpectedValue(), actualPercentage);
        } catch (NumberFormatException nfe) {
            Assert.assertEquals("Should not fail NumberFormatException=" + nfe + " for validity:" + this,
                                Validity.INVALID_NUMBER, getValidity());
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("Should not fail IllegalArgumentException=" + iae + " for validity:" + this,
                                Validity.ILLEGAL_ARG, getValidity());
        }
    }


    /**
     * Test printing
     */
    @Test
    public void testPrint() {
        String actualOutputString = percentageFormatter.print(getExpectedValue(), Locale.getDefault());
        Assert.assertEquals("outputString:" + this, getExpectedOutputString(), actualOutputString);
    }


    /**
     * @return readable representation of our state
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(super.toString());
        sb.append("{percentageFormatter=").append(percentageFormatter);
        sb.append('}');
        return sb.toString();
    }
}
