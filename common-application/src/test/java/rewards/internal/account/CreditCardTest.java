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

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for CreditCard domain object
 *
 * @author Dominic North
 */
public class CreditCardTest {

    /**
     * Test constructor taking number
     */
    @Test
    public void testConstructorNumber() {
        CreditCard creditCard = new CreditCard(AccountTestData.TEST_CREDIT_CARD_0);

        Assert.assertEquals("number", AccountTestData.TEST_CREDIT_CARD_0, creditCard.getCreditCardNumber());
    }

    /**
     * Test Jackson Json marshalling/unmarshalling for annotations in class What
     * goes in should come out!
     */
    @Test
    public void testJsonMarshallUnmarshall() throws Exception {
        CreditCard creditCard = new CreditCard(AccountTestData.TEST_CREDIT_CARD_0);
        ObjectMapper mapper = new ObjectMapper();
        String marshalledVersion = mapper
                .writeValueAsString(creditCard);

        CreditCard unmarshalledCreditCard = mapper.readValue(
                marshalledVersion, CreditCard.class);
        Assert.assertEquals("original and unmarshalled credit card",
                            creditCard, unmarshalledCreditCard);
    }
}