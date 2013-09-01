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

package accounts.web;

import common.datetime.SimpleDate;
import common.money.Percentage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import rewards.internal.account.Account;
import rewards.internal.account.Beneficiary;
import rewards.internal.account.CreditCard;

import java.net.URI;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * End-to-end test for account controller using RestTemplate client
 *
 * @author Dominic North
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class AccountControllerClientTests {

    /**
     * server URIL ending with the servlet mapping on which the application can
     * be reached.
     */
    @Value("#{httpProps.baseUri}")
    private String baseUri;

    private RestTemplate restTemplate = new RestTemplate();
    private Random       random       = new Random();


    /**
     * Test listAccounts
     */
    @Test
    public void listAccounts() {
        String url = baseUri + "/accounts";
        // we have to use Account[] instead of List<Account>, or Jackson won't
        // know what type to un-marshall to
        Account[] accounts = restTemplate.getForObject(url, Account[].class);
        assertTrue(accounts.length >= 21);
        assertEquals("Keith and Keri Donald", accounts[0].getName());
        assertEquals(2, accounts[0].getBeneficiaries().size());
        assertEquals(Percentage.valueOf("50%"),
                     accounts[0].getBeneficiary("Annabelle")
                                .getAllocationPercentage());
    }


    @Test
    public void getAccount0() {
        Account account = doGetAccount(baseUri + "/accounts/{accountId}", 0,
                                       "Keith and Keri Donald", 2);

        assertEquals(Percentage.valueOf("50%"),
                     account.getBeneficiary("Annabelle").getAllocationPercentage());
    }


    @Test
    public void getAccount7() {
        Account account = doGetAccount(baseUri + "/accounts/{accountId}", 7,
                                       "John C. Howard", 0);

        assertNotNull(account);
    }


    /**
     * @param url
     * @param accountId
     * @param expectedName
     * @param expectedBeneficiariesSize
     * @return
     */
    private Account doGetAccount(String url, int accountId,
                                 String expectedName, int expectedBeneficiariesSize) {
        Account account = restTemplate.getForObject(url, Account.class, accountId);
        assertEquals(new Integer(accountId), account.getEntityId());
        assertEquals(expectedName, account.getName());
        assertEquals(expectedBeneficiariesSize, account.getBeneficiaries().size());
        return account;
    }


    @Test
    public void createAccount() {
        String url = baseUri + "/accounts";
        // use a unique number to avoid conflicts
        String number = String.format("%09d", random.nextInt(10000000));
        String creditCardNumber = "5234" + String.format("%012d", random.nextInt(100000000));
        Account account = new Account(number, "John Doe " + number, new SimpleDate(3, 1, 1985).asDate(),
                                      "john.doe@yahoo.com",
                                      true, true, new CreditCard(creditCardNumber));
        account.addBeneficiary("Jane Doe", Percentage.oneHundred());
        URI newAccountLocation = restTemplate.postForLocation(url, account);

        Account retrievedAccount = restTemplate.getForObject(newAccountLocation, Account.class);
        assertEquals("account object", account, retrievedAccount);
        assertNotNull("retrieved has non-null entity ID", retrievedAccount.getEntityId());
    }



    @Test
    public void addAndDeleteBeneficiary() {
        // perform both add and delete to avoid issues with side effects
        String addUrl = baseUri + "/accounts/{accountId}/beneficiaries";
        URI newBeneficiaryLocation = restTemplate.postForLocation(addUrl, "David", 0);
        try {
            Beneficiary newBeneficiary = restTemplate.getForObject(newBeneficiaryLocation, Beneficiary.class);
            assertEquals("David", newBeneficiary.getName());
        } catch (Throwable t) {
            fail("failed to GET beneficiary from URI=" + newBeneficiaryLocation + ":t=" + t);
        }

        restTemplate.delete(newBeneficiaryLocation);
        try {
            restTemplate
                    .getForObject(newBeneficiaryLocation, Beneficiary.class);
            fail("Should have received 404 Not Found after deleting beneficiary at "
                 + newBeneficiaryLocation);
        } catch (HttpClientErrorException e) {
            assertEquals("status code", HttpStatus.NOT_FOUND, e.getStatusCode());
            String responseBody = e.getResponseBodyAsString();
            assertNotNull("response body", responseBody);
            assertTrue("response body should mention 'beneficiary':"
                       + responseBody, responseBody.contains("beneficiary"));
            assertTrue("response body should mention specific name: 'David':"
                       + responseBody, responseBody.contains("David"));
        }
    }


    @Test
    public void createAndDeleteBeneficiary() {
        // perform both create and delete to avoid issues with side effects
        String addUrl = baseUri
                        + "/accounts/{accountId}/beneficiaries/{beneficiaryName}";
        URI newBeneficiaryLocation = restTemplate.postForLocation(addUrl, null,
                                                                  0, "David");

        ResponseEntity<Beneficiary> responseBeneficiary = restTemplate
                .getForEntity(newBeneficiaryLocation, Beneficiary.class);
        Beneficiary newBeneficiary = responseBeneficiary.getBody();
        assertEquals("David", newBeneficiary.getName());
        MediaType contentType = responseBeneficiary.getHeaders()
                                                   .getContentType();
        long expires = responseBeneficiary.getHeaders().getExpires();
        assertEquals("Should use application/json", MediaType.APPLICATION_JSON,
                     new MediaType(contentType.getType(), contentType.getSubtype()));
        assertTrue("Should have non-zero expiry header", expires > 0);

        restTemplate.delete(newBeneficiaryLocation);
        try {
            restTemplate
                    .getForObject(newBeneficiaryLocation, Beneficiary.class);
            fail("Should have received 404 Not Found after deleting beneficiary at "
                 + newBeneficiaryLocation);
        } catch (HttpClientErrorException e) {
            assertEquals("status code", HttpStatus.NOT_FOUND, e.getStatusCode());
            String responseBody = e.getResponseBodyAsString();
            assertNotNull("response body", responseBody);
            assertTrue("response body should mention 'beneficiary':"
                       + responseBody, responseBody.contains("beneficiary"));
            assertTrue("response body should mention specific name: 'David':"
                       + responseBody, responseBody.contains("David"));
        }
    }


    /**
     * Test getting non-existent account
     */
    @Test
    public void getNonExistentAccount() {
        final String url = baseUri + "/accounts/{accountId}";
        try {
            final Account account = restTemplate.getForObject(url, Account.class, 666);
            fail("expected 404 for non-existent account:got account=" + account);
        } catch (HttpStatusCodeException hsce) {
            assertEquals("status code", HttpStatus.NOT_FOUND,
                         hsce.getStatusCode());
            final String responseBody = hsce.getResponseBodyAsString();
            assertNotNull("response body", responseBody);
            assertTrue("response body should mention specific id: 666:"
                       + responseBody, responseBody.contains("id=666"));
        }
    }


    /**
     * Test creation of account with duplicate number
     */
    @Test
    public void createAccountWithDuplicateNumber() {
        String url = baseUri + "/accounts";
        String creditCardNumber = "5234" + String.format("%012d", random.nextInt(100000000));
        Account account = new Account("123456789", "David Doppelganger" + "", new SimpleDate(9, 3, 1971).asDate(),
                                      "double@hotmail.com", false, true,
                                      new CreditCard("4444000011112222"),
                                      new CreditCard(creditCardNumber));
        account.addBeneficiary("Castor");
        try {
            URI location = restTemplate.postForLocation(url, account);
            fail("expected 409 for duplicate number:got location=" + location);
        } catch (HttpStatusCodeException hsce) {
            assertEquals("status code", HttpStatus.CONFLICT,
                         hsce.getStatusCode());
            String responseBody = hsce.getResponseBodyAsString();
            assertNotNull("response body", responseBody);
            final String expectedAccountString = "duplicate accountNumber='123456789'";
            assertTrue("response body should mention '" + expectedAccountString
                       + "':" + responseBody,
                       responseBody.contains(expectedAccountString));
        }
    }

}
