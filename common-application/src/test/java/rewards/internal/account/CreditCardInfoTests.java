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

package rewards.internal.account;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for CreditCardInfo DTO
 *
 * @author Dominic North
 */
public class CreditCardInfoTests {

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        CreditCardInfo creditCardInfo = new CreditCardInfo();

        Assert.assertNull("number", creditCardInfo.getCreditCardNumber());
    }

    /**
     * Test constructor taking number
     */
    @Test
    public void testConstructorNumber() {
        CreditCardInfo creditCardInfo = new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0);

        Assert.assertEquals("number", AccountTestData.TEST_CREDIT_CARD_0, creditCardInfo.getCreditCardNumber());
    }

    /**
     * Test set number
     */
    @Test
    public void testSetNumber() {
        CreditCardInfo creditCardInfo = new CreditCardInfo();
        creditCardInfo.setCreditCardNumber(AccountTestData.TEST_CREDIT_CARD_0);

        Assert.assertEquals("number", AccountTestData.TEST_CREDIT_CARD_0, creditCardInfo.getCreditCardNumber());
    }

    /**
     * Test createCreditCard
     */
    @Test
    public void testCreateCreditCard() {
        CreditCardInfo creditCardInfo = new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0);
        CreditCard creditCard = creditCardInfo.createCreditCard();

        Assert.assertEquals("creditCard number", AccountTestData.TEST_CREDIT_CARD_0, creditCard.getCreditCardNumber());
    }

    /**
     * Test createCreditCard null number
     */
    @Test(expected = IllegalStateException.class)
    public void testCreateCreditCardNullNumber() {
        CreditCardInfo creditCardInfo = new CreditCardInfo();
        CreditCard creditCard = creditCardInfo.createCreditCard();
    }


    /**
     * Ensure correct behaviour in a set i.e. hashCode changes when mutable fields modified except for number
     */
    @Test
    public void testHashSetBehaviour() {
        CreditCardInfo creditCardInfo = new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0);

        Set<CreditCardInfo> creditCardInfoSet = new HashSet<CreditCardInfo>();
        creditCardInfoSet.add(creditCardInfo);

        Assert.assertTrue("creditCardInfoSet.contains(creditCardInfo) should be true",
                          creditCardInfoSet.contains(creditCardInfo));

        creditCardInfo.setCreditCardNumber(AccountTestData.TEST_CREDIT_CARD_1);
        Assert.assertFalse("creditCardInfoSet.contains(creditCardInfo) should be false after setCreditCardNumber",
                           creditCardInfoSet.contains(creditCardInfo));
    }

}
