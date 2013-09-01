package common.money;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * User: djnorth
 * Date: 15/11/2012
 * Time: 14:53
 */
@RunWith(Parameterized.class)
public abstract class PercentageTestBase {

    protected enum Validity {
        VALID,
        INVALID_NUMBER,
        ILLEGAL_ARG
    }

    /**
     * Method to get parameters for tests
     */
    @Parameterized.Parameters
    public static List<Object[]> getData() {
        Object[][] data = {
                {"100%", Validity.VALID, BigDecimal.valueOf(1), BigDecimal.valueOf(1), "100%"},
                {"50%", Validity.VALID, BigDecimal.valueOf(0.5), BigDecimal.valueOf(0.5), "50%"},
                {"0.05", Validity.VALID, BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.05), "5%"},
                {"23.5%", Validity.VALID, BigDecimal.valueOf(0.235), BigDecimal.valueOf(0.24), "24%"},
                {"0.056", Validity.VALID, BigDecimal.valueOf(0.056), BigDecimal.valueOf(0.06), "6%"},
                {"0.054", Validity.VALID, BigDecimal.valueOf(0.054), BigDecimal.valueOf(0.05), "5%"},
                {"10%%", Validity.INVALID_NUMBER, null, null, null},
                {"%", Validity.INVALID_NUMBER, null, null, null},
                {"10", Validity.ILLEGAL_ARG, null, null, null},
                {null, Validity.ILLEGAL_ARG, null, null, null},
                {"", Validity.ILLEGAL_ARG, null, null, null}
        };

        return Arrays.asList(data);
    }


    /**
     * Input string
     */
    private final String inputString;

    /**
     * Valid input flag
     */
    private final Validity validity;

    /**
     * Construction value (BigDecimal)
     */
    private final BigDecimal constructionValueBigDecimal;

    /**
     * Expected value (BigDecimal)
     */
    private final BigDecimal expectedValueBigDecimal;

    /**
     * Expected output from expected MonetaryAmount value
     */
    private final String expectedOutputString;

    /**
     * Constructor setting data fields
     *
     * @param inputString
     * @param validity
     * @param constructionValueBigDecimal
     * @param expectedValueBigDecimal
     * @param expectedOutputString
     */
    public PercentageTestBase(String inputString,
                              Validity validity, BigDecimal constructionValueBigDecimal,
                              BigDecimal expectedValueBigDecimal, String expectedOutputString) {
        this.inputString = inputString;
        this.expectedOutputString = expectedOutputString;
        this.validity = validity;
        this.constructionValueBigDecimal = constructionValueBigDecimal;
        this.expectedValueBigDecimal = expectedValueBigDecimal;
    }


    /**
     * @return readable representation of our state
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName());
        sb.append('{');
        appendFieldValue(sb, "inputString", inputString);
        sb.append(", validity=").append(validity);
        sb.append(", constructionValueBigDecimal=").append(constructionValueBigDecimal);
        sb.append(", expectedValueBigDecimal=").append(expectedValueBigDecimal);
        appendFieldValue(sb, ", expectedOutputString=", getExpectedOutputString());
        sb.append('}');
        return sb.toString();
    }


    /**
     * toString helper
     *
     * @param sb         StringBuffer to which we append
     * @param fieldName
     * @param fieldValue
     */
    private void appendFieldValue(StringBuffer sb, String fieldName, String fieldValue) {
        sb.append(fieldName).append('=');
        if (fieldValue == null) {
            sb.append(fieldValue);
        } else {
            sb.append('\'').append(fieldValue).append('\'');
        }
    }

    /**
     * @return input string
     */
    protected String getInputString() {
        return inputString;
    }


    /**
     * @return validity
     */
    protected Validity getValidity() {
        return validity;
    }


    /**
     * @return constructionValueBigDecimal
     */
    public BigDecimal getConstructionValueBigDecimal() {
        return constructionValueBigDecimal;
    }


    /**
     * @return expected value as Percentage
     */
    protected Percentage getExpectedValue() {
        return (expectedValueBigDecimal == null ? null : new Percentage(expectedValueBigDecimal));
    }


    /**
     * @return expectedValueBigDecimal as it is (BigDecimal)
     */
    protected BigDecimal getExpectedValueBigDecimal() {
        return expectedValueBigDecimal;
    }


    /**
     * Expected string from expectedValueBigDecimal (not tested if null)
     */
    protected String getExpectedOutputString() {
        return expectedOutputString;
    }
}
