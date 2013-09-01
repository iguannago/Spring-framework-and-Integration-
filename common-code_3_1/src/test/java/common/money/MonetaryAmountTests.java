package common.money;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * Unit tests for {@link MonetaryAmount}
 *
 * @author Dominic North
 */
public class MonetaryAmountTests extends MonetaryAmountTestBase {

    /**
     * Constructor setting data fields
     *
     * @param inputString
     * @param validInput
     * @param constructionValueBigDecimal
     * @param expectedValueBigDecimal
     * @param expectedOutputString
     */
    public MonetaryAmountTests(String inputString, boolean validInput, BigDecimal constructionValueBigDecimal,
                               BigDecimal expectedValueBigDecimal, String expectedOutputString) {
        super(inputString, validInput, constructionValueBigDecimal, expectedValueBigDecimal, expectedOutputString);
    }


    /**
     * Test construction from BigDecimal
     */
    @Test
    public void testConstructorBigDecimal() {
        if (getConstructionValueBigDecimal() != null) {
            MonetaryAmount actualAmount = new MonetaryAmount(getConstructionValueBigDecimal());
            Assert.assertEquals(getExpectedValue(), actualAmount);
        } else {
            try {
                new MonetaryAmount(getConstructionValueBigDecimal());
                Assert.fail("should give exception on construction with null argument");
            } catch (NullPointerException npe) {
                //
            }
        }
    }


    /**
     * Test construction from double
     */
    @Test
    public void testConstructorDouble() {
        if (getConstructionValueBigDecimal() != null) {
            MonetaryAmount actualAmount = new MonetaryAmount(getConstructionValueBigDecimal().doubleValue());
            Assert.assertEquals(getExpectedValue(), actualAmount);
        }
    }


    /**
     * Test parsing
     */
    @Test
    public void testValueOf() {
        if (StringUtils.hasText(getInputString())) {
            try {
                MonetaryAmount actualAmount = MonetaryAmount.valueOf(getInputString());
                Assert.assertTrue("should throw exception for invalid input:" + this, isValidInput());
                Assert.assertEquals("expectedValue:" + this, getExpectedValue(), actualAmount);
            } catch (NumberFormatException nfe) {
                Assert.assertFalse("should not throw exception=" + nfe + " for valid input:" + this, isValidInput());
            }
        } else {
            try {
                MonetaryAmount.valueOf(getInputString());
                Assert.fail("Should get IllegalArgumentException for inputString:" + this);
            } catch (IllegalArgumentException iae) {
                //
            }
        }
    }


    /**
     * Test printing
     */
    @Test
    public void testToString() {
        if (getExpectedValue() != null) {
            String actualOutputString = getExpectedValue().toString();
            Assert.assertEquals("outputString:" + this, getExpectedOutputString(), actualOutputString);
        }
    }


    /**
     * Check JSON marshalling
     */
    @Test
    public void testJsonMarshallUnmarshall() throws Exception {
        MonetaryAmount monetaryAmount = getExpectedValue();
        ObjectMapper mapper = new ObjectMapper();
        String marshalledAmount = mapper.writeValueAsString(monetaryAmount);
        MonetaryAmount unmarshalledMonetaryAmount = mapper.readValue(marshalledAmount, MonetaryAmount.class);
        Assert.assertEquals("original and unmarshalled amounts", monetaryAmount, unmarshalledMonetaryAmount);
    }
}
