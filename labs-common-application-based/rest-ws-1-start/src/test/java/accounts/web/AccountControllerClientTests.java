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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    @Ignore
    public void listAccounts() {
        String url = baseUri + "/accounts";
        // we have to use Account[] instead of List<Account>, or Jackson won't
        // know what type to un-marshall to
        Account[] accounts = null;
        // TODO 02: Use restTemplate to get list of account, remove @Ignore
        assertTrue(accounts.length >= 21);
        assertEquals("Keith and Keri Donald", accounts[0].getName());
        assertEquals(2, accounts[0].getBeneficiaries().size());
        assertEquals(Percentage.valueOf("50%"),
                     accounts[0].getBeneficiary("Annabelle")
                                .getAllocationPercentage());
    }

    @Test
    @Ignore
    public void getAccount0() {
        Account account = doGetAccount(baseUri + "/accounts/{accountId}", 0,
                                       "Keith and Keri Donald", 2);

        assertEquals(Percentage.valueOf("50%"),
                     account.getBeneficiary("Annabelle").getAllocationPercentage());
    }


    @Test
    @Ignore
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
        Account account = null;
        // TODO 04: Get account using supplied ID, remove @Ignore from getAccount0 and 7 above
        assertEquals(new Integer(accountId), account.getEntityId());
        assertEquals(expectedName, account.getName());
        assertEquals(expectedBeneficiariesSize, account.getBeneficiaries().size());
        return account;
    }


    @Test
    @Ignore
    public void createAccount() {
        String url = baseUri + "/accounts";
        // use a unique number to avoid conflicts
        String number = String.format("%09d", random.nextInt(10000000));
        String creditCardNumber = "5234" + String.format("%012d", random.nextInt(100000000));
        Account account = new Account(number, "John Doe " + number, new SimpleDate(3, 1, 1985).asDate(),
                                      "john.doe@yahoo.com",
                                      true, true, new CreditCard(creditCardNumber));
        account.addBeneficiary("Jane Doe", Percentage.oneHundred());
        URI newAccountLocation = null;
        // TODO 08: Create account, retrieving location, remove @Ignore above
        Account retrievedAccount = restTemplate.getForObject(
                newAccountLocation, Account.class);
        assertEquals("account object", account, retrievedAccount);
        assertNotNull("retrieved has non-null entity ID", retrievedAccount.getEntityId());
    }


    @Test
    @Ignore
    public void addAndDeleteBeneficiary() {
        // perform both add and delete to avoid issues with side effects
        String addUrl = baseUri + "/accounts/{accountId}/beneficiaries";

        // TODO 16: Add beneficiary, retrieving location, then retrieve using location
        URI newBeneficiaryLocation = null;
        Beneficiary newBeneficiary = null;
        assertEquals("David", newBeneficiary.getName());
        // TODO 17: Delete beneficiary

        try {
            // TODO 18: Retrieve beneficiary again, to verify deletion, remove @Ignore above
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
    @Ignore //TODO 20: Remove ignore
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
    @Ignore
    public void createAccountWithDuplicateNumber() {
        String url = baseUri + "/accounts";
        String creditCardNumber = "5234" + String.format("%012d", random.nextInt(100000000));
        Account account = new Account("123456789", "David Doppelganger" + "", new SimpleDate(9, 3, 1971).asDate(),
                                      "double@hotmail.com", false, true,
                                      new CreditCard("4444000011112222"),
                                      new CreditCard(creditCardNumber));
        account.addBeneficiary("Castor");
        try {
            URI location = null;
            // TODO 23: Attempt to create account with a duplicate account number
            fail("expected 409 for duplicate number:got location=" + location);
        } catch (HttpStatusCodeException hsce) {
            // TODO 24: Verify correct behaviour for creation with duplicate account number, remove @Ignore above
            String responseBody = hsce.getResponseBodyAsString();
            assertNotNull("response body", responseBody);
            final String expectedAccountString = "duplicate accountNumber='123456789'";
            assertTrue("response body should mention '" + expectedAccountString
                       + "':" + responseBody,
                       responseBody.contains(expectedAccountString));
        }
    }
}
