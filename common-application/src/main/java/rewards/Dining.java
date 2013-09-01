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
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * A dining event that occurred, representing a charge made to an credit card by a merchant on a specific date.
 * <p/>
 * For a dining to be eligible for reward, the credit card number should map to an account in the reward network. In
 * addition, the merchant number should map to a restaurant in the network.
 * <p/>
 * A value object. Immutable.
 */
public class Dining implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @NotNull
    @DecimalMin("0.0")
    private float amount;

    @NotNull
    @Pattern(regexp = "\\d{16}")
    private String creditCardNumber;

    @NotNull
    @Pattern(regexp = "\\d{9}")
    private String merchantNumber;

    @NotNull
    private Date date;

    /**
     * Creates a new dining, reflecting an amount that was charged to a card by a merchant on the date specified.
     *
     * @param amount           the total amount of the dining bill
     * @param creditCardNumber the number of the credit card used to pay for the dining bill
     * @param merchantNumber   the merchant number of the restaurant where the dining occurred
     * @param date             the date of the dining event
     */
    public Dining(float amount, String creditCardNumber, String merchantNumber, SimpleDate date) {
        this.amount = amount;
        this.creditCardNumber = creditCardNumber;
        this.merchantNumber = merchantNumber;
        this.date = date.asDate();
    }

    /**
     * Creates a new dining, reflecting an amount that was charged to a card by a merchant on the date specified.
     *
     * @param amount           the total amount of the dining bill
     * @param creditCardNumber the number of the credit card used to pay for the dining bill
     * @param merchantNumber   the merchant number of the restaurant where the dining occurred
     * @param dateTime         the date and time of the dining event
     */
    @JsonCreator
    public Dining(@JsonProperty("amount") float amount, @JsonProperty("creditCardNumber") String creditCardNumber,
                  @JsonProperty("merchantNumber") String merchantNumber, @JsonProperty("dateTime") Date dateTime) {
        this.amount = amount;
        this.creditCardNumber = creditCardNumber;
        this.merchantNumber = merchantNumber;
        this.date = dateTime;
    }

    /**
     * Creates a new dining, reflecting an amount that was charged to a credit card by a merchant on today's date. A
     * convenient static factory method.
     *
     * @param amount           the total amount of the dining bill as a string
     * @param creditCardNumber the number of the credit card used to pay for the dining bill
     * @param merchantNumber   the merchant number of the restaurant where the dining occurred
     * @return the dining event
     */
    public static Dining createDining(String amount, String creditCardNumber, String merchantNumber) {
        return new Dining(new Float(amount), creditCardNumber, merchantNumber, Calendar.getInstance().getTime());
    }

    /**
     * Creates a new dining, reflecting an amount that was charged to a credit card by a merchant on the date specified.
     * A convenient static factory method.
     *
     * @param amount           the total amount of the dining bill as a string
     * @param creditCardNumber the number of the credit card used to pay for the dining bill
     * @param merchantNumber   the merchant number of the restaurant where the dining occurred
     * @param month            the month of the dining event
     * @param day              the day of the dining event
     * @param year             the year of the dining event
     * @return the dining event
     */
    public static Dining createDining(String amount, String creditCardNumber, String merchantNumber, int month,
                                      int day, int year) {
        return new Dining(new Float(amount), creditCardNumber, merchantNumber, new SimpleDate(month, day,
                                                                                              year));
    }

    /**
     * Returns the amount of this dining--the total amount of the bill that was charged to the credit card.
     */
    public float getAmount() {
        return amount;
    }

    /**
     * Returns the number of the credit card used to pay for this dining. For this dining to be eligible for reward,
     * this credit card number should be associated with a valid account in the reward network.
     */
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    /**
     * Returns the merchant number of the restaurant where this dining occurred. For this dining to be eligible for
     * reward, this merchant number should be associated with a valid restaurant in the reward network.
     */
    public String getMerchantNumber() {
        return merchantNumber;
    }

    /**
     * Returns the date-time this dining occurred on.
     */
    public Date getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dining)) {
            return false;
        }

        Dining dining = (Dining) o;

        if (Float.compare(dining.amount, amount) != 0) {
            return false;
        }
        if (creditCardNumber != null ? !creditCardNumber.equals(dining.creditCardNumber) :
            dining.creditCardNumber != null) {
            return false;
        }
        if (date != null ? !date.equals(dining.date) : dining.date != null) {
            return false;
        }
        if (merchantNumber != null ? !merchantNumber.equals(dining.merchantNumber) : dining.merchantNumber != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (amount != +0.0f ? Float.floatToIntBits(amount) : 0);
        result = 31 * result + (creditCardNumber != null ? creditCardNumber.hashCode() : 0);
        result = 31 * result + (merchantNumber != null ? merchantNumber.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Dining");
        sb.append("{amount=").append(amount);
        sb.append(", creditCardNumber='").append(creditCardNumber).append('\'');
        sb.append(", merchantNumber='").append(merchantNumber).append('\'');
        sb.append(", date=").append(date);
        sb.append('}');
        return sb.toString();
    }
}