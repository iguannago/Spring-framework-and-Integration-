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

package rewards.ws;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringSource;

import javax.xml.transform.Source;

import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SoapRewardIntegrationTests {

    MockWebServiceClient mockClient;

    @Value("#{testMessages.validRequest}")
    private String validRequest;
    @Value("#{testMessages.validResponse}")
    private String validResponse;
    @Value("#{testMessages.invalidRequestMissingCreditCardNumber}")
    private String invalidRequestMissingCreditCardNumber;
    @Value("#{testMessages.invalidRequestBadCreditCardNumber}")
    private String invalidRequestBadCreditCardNumber;

    @Autowired
    public void createClient(ApplicationContext applicationContext) {
        // TODO-02 Autowire the ApplicationContext into a field and use it
        // to create the MockWebServiceClient
        mockClient = MockWebServiceClient.createClient(applicationContext);
    }

    @Test
    @DirtiesContext
    public void validRequest() {
        // TODO-03 Create Source variables for a valid request and response
        // pair
        // and verify that the request results in the given response

        Source requestSource = new StringSource(validRequest);
        Source responseSource = new StringSource(validResponse);

        mockClient.sendRequest(withPayload(requestSource)).andExpect(
                payload(responseSource));
    }

    @Test
    @DirtiesContext
    public void validRequestXPath() {
        Source requestSource = new StringSource(validRequest);

        mockClient.sendRequest(withPayload(requestSource))
                  .andExpect(xpath("//@amount").evaluatesTo("8.00"))
                  .andExpect(xpath("//@accountNumber").evaluatesTo("123456789"))
                  .andExpect(xpath("//@confirmationNumber").evaluatesTo("1"));
    }

    @Test
    public void invalidRequestWithoutCreditCardNumber() {
        // TODO-05 Based on the request Source of the previous test,
        // create a request with a missing creditCardNumber attribute
        // and verify that it results in a 'Client' or 'Sender' fault
        Source requestSource = new StringSource(invalidRequestMissingCreditCardNumber);
        mockClient.sendRequest(withPayload(requestSource)).andExpect(clientOrSenderFault());
    }

    @Test
    public void invalidRequestWithUnknownCreditCardNumber() {
        // TODO-07 Based on the request Source of the first test,
        // create a request with an unknown creditCardNumber attribute
        // and verify that it results in a 'client' or 'sender' fault
        Source requestSource = new StringSource(invalidRequestBadCreditCardNumber);

        mockClient.sendRequest(withPayload(requestSource)).andExpect(clientOrSenderFault());
    }

}