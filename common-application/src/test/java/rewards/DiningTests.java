/*
 *
 *  * Copyright 2002-2011 the original author or authors, or Red-Black IT Ltd, as appropriate.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package rewards;

import common.datetime.SimpleDate;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Unit tests for Dining DTO class
 *
 * @author Dominic North
 */
@RunWith(Parameterized.class)
public class DiningTests {

    /**
     * Static method for test data
     */
    @Parameterized.Parameters
    public static List<Object[]> testData() {

        Object[][] data = {
                {100.00f, "1234123412341234", "123456789", DateTime.now()},
                {72.33f, "1234123412340001", "123456780", DateMidnight.now().toDateTime()},
                {12.50f, "4929123412340001", "423456780", new DateTime(1957, 10, 4, 10, 18, 13)}
        };

        return Arrays.asList(data);
    }

    /**
     * Test values
     */
    private final float    amount;
    private final String   creditCardNumber;
    private final String   merchantNumber;
    private final DateTime dateTime;

    public DiningTests(float amount, String creditCardNumber, String merchantNumber, DateTime dateTime) {
        this.amount = amount;
        this.creditCardNumber = creditCardNumber;
        this.merchantNumber = merchantNumber;
        this.dateTime = dateTime;
    }


    /**
     * Test constructor with SimpleDate
     */
    @Test
    public void testConstructSimpleDate() {

        final SimpleDate simpleDate =
                new SimpleDate(dateTime.getMonthOfYear(), dateTime.getDayOfMonth(), dateTime.getYear());
        final Date expectedDate = dateTime.toDateMidnight().toDate();
        Dining dining = new Dining(amount, creditCardNumber, merchantNumber, simpleDate);

        Assert.assertEquals("amount", amount, dining.getAmount(), 0f);
        Assert.assertEquals("creditCardNumber", creditCardNumber, dining.getCreditCardNumber());
        Assert.assertEquals("merchantNumber", merchantNumber, dining.getMerchantNumber());
        Assert.assertEquals("date", expectedDate, dining.getDate());
    }


    /**
     * Test constructor with java.util.Date
     */
    @Test
    public void testConstructDate() {
        final Date expectedDate = dateTime.toDate();
        Dining dining = new Dining(amount, creditCardNumber, merchantNumber, expectedDate);

        Assert.assertEquals("amount", amount, dining.getAmount(), 0f);
        Assert.assertEquals("creditCardNumber", creditCardNumber, dining.getCreditCardNumber());
        Assert.assertEquals("merchantNumber", merchantNumber, dining.getMerchantNumber());
        Assert.assertEquals("date", expectedDate, dining.getDate());
    }


    /**
     * Test factory method defaulting date
     */
    @Test
    public void testCreateNoDate() {
        final Date lowDate = new Date();
        Dining dining = Dining.createDining(Float.toString(amount), creditCardNumber, merchantNumber);
        final Date highDate = new Date();

        Assert.assertEquals("amount", amount, dining.getAmount(), 0f);
        Assert.assertEquals("creditCardNumber", creditCardNumber, dining.getCreditCardNumber());
        Assert.assertEquals("merchantNumber", merchantNumber, dining.getMerchantNumber());
        Assert.assertTrue("date <= low date=" + lowDate, dining.getDate().compareTo(lowDate) >= 0);
        Assert.assertTrue("date >= high date=" + highDate, dining.getDate().compareTo(highDate) <= 0);
    }


    /**
     * Test factory method using day, month, year
     */
    @Test
    public void testCreateDayMonthYear() {
        Dining dining =
                Dining.createDining(Float.toString(amount), creditCardNumber, merchantNumber, dateTime.getMonthOfYear(),
                                    dateTime.getDayOfMonth(), dateTime.getYear());
        final Date expectedDate = dateTime.toDateMidnight().toDate();

        Assert.assertEquals("amount", amount, dining.getAmount(), 0f);
        Assert.assertEquals("creditCardNumber", creditCardNumber, dining.getCreditCardNumber());
        Assert.assertEquals("merchantNumber", merchantNumber, dining.getMerchantNumber());
        Assert.assertEquals("date", expectedDate, dining.getDate());
    }

    /**
     * Test same object equals
     */
    @Test
    public void testSameEquals() {
        final Date date = dateTime.toDate();
        Dining dining = new Dining(amount, creditCardNumber, merchantNumber, date);

        Assert.assertEquals("same dining", dining, dining);
    }

    /**
     * Test same object equals
     */
    @Test
    public void testEqualValuesEquals() {
        final Date date = dateTime.toDate();
        Dining dining0 = new Dining(amount, creditCardNumber, merchantNumber, date);
        Dining dining1 = new Dining(amount, creditCardNumber, merchantNumber, date);

        Assert.assertEquals("equal values", dining0, dining1);
    }

    /**
     * Test different date by milliseconds not equal
     */
    @Test
    public void testDiffMillisNotEqual() {
        final DateTime dateTimePlus100 = dateTime.plus(100);
        Dining dining0 = new Dining(amount, creditCardNumber, merchantNumber, dateTime.toDate());
        Dining dining1 = new Dining(amount, creditCardNumber, merchantNumber, dateTimePlus100.toDate());

        Assert.assertFalse("unequal values (100ms):dining0=" + dining0 + ":dining1=" + dining1,
                           dining0.equals(dining1));
    }

    /**
     * Test different date by 1 day not equal
     */
    @Test
    public void testDiffDayOfMonthNotEqual() {
        final DateTime dateTimePlus1Day = dateTime.plusDays(1);
        Dining dining0 = new Dining(amount, creditCardNumber, merchantNumber, dateTime.toDate());
        Dining dining1 = new Dining(amount, creditCardNumber, merchantNumber, dateTimePlus1Day.toDate());

        Assert.assertFalse("unequal values (1 day):dining0=" + dining0 + ":dining1=" + dining1,
                           dining0.equals(dining1));
    }

    /**
     * Test different date different year not equal
     */
    @Test
    public void testDiffYearNotEqual() {
        final DateTime dateTimePlus1Year = dateTime.plusYears(1);
        Dining dining0 = new Dining(amount, creditCardNumber, merchantNumber, dateTime.toDate());
        Dining dining1 = new Dining(amount, creditCardNumber, merchantNumber, dateTimePlus1Year.toDate());

        Assert.assertFalse("unequal values (1 year):dining0=" + dining0 + ":dining1=" + dining1,
                           dining0.equals(dining1));
    }

    /**
     * Test different amount not equal
     */
    @Test
    public void testDiffAmountNotEqual() {
        final Date date = dateTime.toDate();
        float amountPlus10p5 = amount + 10.5f;
        Dining dining0 = new Dining(amount, creditCardNumber, merchantNumber, date);
        Dining dining1 = new Dining(amountPlus10p5, creditCardNumber, merchantNumber, date);

        Assert.assertFalse("unequal values (amount):dining0=" + dining0 + ":dining1=" + dining1,
                           dining0.equals(dining1));
    }

    /**
     * Test different amount not equal
     */
    @Test
    public void testDiffCreditCardNotEqual() {
        final Date date = dateTime.toDate();
        final String newFirstChar = (creditCardNumber.charAt(0) == '4' ? "5" : "4");
        final String creditCardNumberFlipDigit = newFirstChar + creditCardNumber.substring(1);
        Dining dining0 = new Dining(amount, creditCardNumber, merchantNumber, date);
        Dining dining1 = new Dining(amount, creditCardNumberFlipDigit, merchantNumber, date);

        Assert.assertFalse("unequal values (creditCardNumber):dining0=" + dining0 + ":dining1=" + dining1,
                           dining0.equals(dining1));
    }

    /**
     * Test different amount not equal
     */
    @Test
    public void testDiffMerchantNotEqual() {
        final Date date = dateTime.toDate();
        final String newFirstChar = (merchantNumber.charAt(0) == '4' ? "5" : "4");
        final String merchantNumberFlipDigit = newFirstChar + merchantNumber.substring(1);
        Dining dining0 = new Dining(amount, creditCardNumber, merchantNumber, date);
        Dining dining1 = new Dining(amount, creditCardNumber, merchantNumberFlipDigit, date);

        Assert.assertFalse("unequal values (merchantNumber):dining0=" + dining0 + ":dining1=" + dining1,
                           dining0.equals(dining1));
    }


    /**
     * Test Jackson Json marshalling/unmarshalling:
     * what goes in should come out!
     */
    @Test
    public void testJsonMarshallUnmarshall() throws Exception {
        Dining dining = new Dining(amount, creditCardNumber, merchantNumber, dateTime.toDate());
        ObjectMapper mapper = new ObjectMapper();
        String marshalledVersion = mapper.writeValueAsString(dining);
        Dining unmarshalledDining = mapper.readValue(marshalledVersion, Dining.class);
        Assert.assertEquals("original and unmarshalled dining", dining, unmarshalledDining);
    }

}
