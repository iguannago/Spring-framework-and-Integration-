package common.money;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;

/**
 * Unit tests for {@link Percentage}
 *
 * @author Dominic North
 */
@RunWith(Parameterized.class)
public class PercentageTests extends PercentageTestBase {

    /**
     * Constructor setting data fields
     *
     * @param inputString
     * @param validity
     * @param constructionValueBigDecimal
     * @param expectedValueBigDecimal
     * @param expectedOutputString
     */
    public PercentageTests(String inputString, Validity validity,
                           BigDecimal constructionValueBigDecimal, BigDecimal expectedValueBigDecimal,
                           String expectedOutputString) {
        super(inputString, validity, constructionValueBigDecimal, expectedValueBigDecimal, expectedOutputString);
    }


    /**
     * Test constructor taking BigDecimal
     */
    @Test
    public void testConstructorBigDecimal() {
        if (getConstructionValueBigDecimal() != null) {
            try {
                Percentage actualPercentage = new Percentage(getConstructionValueBigDecimal());
                Assert.assertEquals("Should fail for invalid value:" + this, Validity.VALID, getValidity());
                Assert.assertEquals("expectedValue:" + this, getExpectedValue(), actualPercentage);
            } catch (NumberFormatException nfe) {
                Assert.assertEquals("Should not fail NumberFormatException=" + nfe + " for validity:" + this,
                                    Validity.INVALID_NUMBER, getValidity());
            } catch (IllegalArgumentException iae) {
                Assert.assertEquals("Should not fail IllegalArgumentException=" + iae + " for validity:" + this,
                                    Validity.ILLEGAL_ARG, getValidity());
            }
        } else {
            try {
                new Percentage(getConstructionValueBigDecimal());
                Assert.fail("should give exception on construction with null argument");
            } catch (NullPointerException npe) {
                //
            }
        }

    }


    /**
     * Test constructor taking double
     */
    @Test
    public void testConstructorDouble() {
        if (getConstructionValueBigDecimal() != null) {
            try {
                Percentage actualPercentage = new Percentage(getConstructionValueBigDecimal().doubleValue());
                Assert.assertEquals("Should fail for invalid value:" + this, Validity.VALID, getValidity());
                Assert.assertEquals("expectedValue:" + this, getExpectedValue(), actualPercentage);
            } catch (NumberFormatException nfe) {
                Assert.assertEquals("Should not fail NumberFormatException=" + nfe + " for validity:" + this,
                                    Validity.INVALID_NUMBER, getValidity());
            } catch (IllegalArgumentException iae) {
                Assert.assertEquals("Should not fail IllegalArgumentException=" + iae + " for validity:" + this,
                                    Validity.ILLEGAL_ARG, getValidity());
            }
        }
    }


    /**
     * Test valueOf
     */
    @Test
    public void testValueOf() {
        try {
            Percentage actualPercentage = Percentage.valueOf(getInputString());
            Assert.assertEquals("Should fail for invalid value:" + this, getValidity(), Validity.VALID);
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
     * Check JSON marshalling
     */
    @Test
    public void testJsonMarshallUnmarshall() throws Exception {
        if (getValidity() == Validity.VALID) {
            Percentage percentageUnderTest = getExpectedValue();
            ObjectMapper mapper = new ObjectMapper();
            String marshalledPercentage = mapper.writeValueAsString(percentageUnderTest);
            Percentage unmarshalledPercentage = mapper.readValue(marshalledPercentage, Percentage.class);
            Assert.assertEquals("original and unmarshalled percentages", percentageUnderTest, unmarshalledPercentage);
        }
    }
}
