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
public abstract class MonetaryAmountTestBase {

    /**
     * Method to get parameters for tests
     */
    @Parameterized.Parameters
    public static List<Object[]> getData() {
        Object[][] data = {
                {"$100.00", true, BigDecimal.valueOf(100), BigDecimal.valueOf(100), "$100.00"},
                {"100", true, BigDecimal.valueOf(100), BigDecimal.valueOf(100), "$100.00"},
                {"2200.5", true, BigDecimal.valueOf(2200.5), BigDecimal.valueOf(2200.5), "$2200.50"},
                {"100.5", true, BigDecimal.valueOf(100.5), BigDecimal.valueOf(100.5), "$100.50"},
                {"10.555", true, BigDecimal.valueOf(10.555), BigDecimal.valueOf(10.56), "$10.56"},
                {"410.534", true, BigDecimal.valueOf(410.534), BigDecimal.valueOf(410.53), "$410.53"},
                {"£100", false, null, null, null},
                {null, true, null, null, null},
                {"", true, null, null, null}
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
    private final boolean validInput;

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
     * @param validInput
     * @param constructionValueBigDecimal
     * @param expectedValueBigDecimal
     * @param expectedOutputString
     */
    public MonetaryAmountTestBase(String inputString,
                                  boolean validInput, BigDecimal constructionValueBigDecimal,
                                  BigDecimal expectedValueBigDecimal, String expectedOutputString) {
        this.inputString = inputString;
        this.expectedOutputString = expectedOutputString;
        this.validInput = validInput;
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
        sb.append(", validInput=").append(validInput);
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
     * @return valid input flag
     */
    protected boolean isValidInput() {
        return validInput;
    }


    /**
     *
     * @return constructionValueBigDecimal
     */
    public BigDecimal getConstructionValueBigDecimal() {
        return constructionValueBigDecimal;
    }


    /**
     * @return expected value as MonetaryAmount
     */
    protected MonetaryAmount getExpectedValue() {
        return (expectedValueBigDecimal == null ? null : new MonetaryAmount(expectedValueBigDecimal));
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
