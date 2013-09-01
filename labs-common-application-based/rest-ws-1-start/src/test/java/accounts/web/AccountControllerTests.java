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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import rewards.AccountServiceManager;
import rewards.internal.account.Account;
import rewards.internal.account.Beneficiary;
import rewards.internal.account.CreditCard;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Unit tests for the AccountController.
 *
 * @author Dominic North
 */
public class AccountControllerTests {

    private static final int        ACCOUNT0_ID          = 0;
    private static final int        ACCOUNT1_ID          = 1;
    private static final String     TEST_BENEFICIARY_0_0 = "TestBeneficiary0-0";
    private static final String     TEST_BENEFICIARY_0_1 = "TestBeneficiary0-1";
    private static final String     TEST_BENEFICIARY_1_0 = "TestBeneficiary1-0";
    private static final String     TEST_BENEFICIARY_1_1 = "TestBeneficiary1-1";
    private static final String     TEST_BENEFICIARY_1_2 = "TestBeneficiary1-2";
    private static final Percentage PERCENTAGE_50PC      = new Percentage(0.5);
    private static final Percentage PERCENTAGE_25PC      = new Percentage(0.25);

    private AccountController controller;

    private AccountServiceManager accountManager;

    /**
     * Set-up accountManager mock object
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        accountManager = mock(AccountServiceManager.class);
        controller = new AccountController(accountManager);
    }


    /**
     * Test accountSummary (list)
     */
    @Test
    public void testAccountSummary() {
        final Account account0 = createAccount0();
        account0.addBeneficiary(TEST_BENEFICIARY_0_0, PERCENTAGE_50PC);
        account0.addBeneficiary(TEST_BENEFICIARY_0_1, PERCENTAGE_50PC);

        final Account account1 = new Account("123457891", "Test Account 1", new SimpleDate(2, 2, 1965).asDate(),
                                             "extra@live.co.uk", false, true, new CreditCard("4500123456789000"));

        account1.setEntityId(1);
        account1.addBeneficiary(TEST_BENEFICIARY_1_0, PERCENTAGE_50PC);
        account1.addBeneficiary(TEST_BENEFICIARY_1_1, PERCENTAGE_25PC);
        account1.addBeneficiary(TEST_BENEFICIARY_1_2, PERCENTAGE_25PC);

        final List<Account> expectedAccounts = new ArrayList<Account>();
        expectedAccounts.add(account0);
        expectedAccounts.add(account1);

        when(accountManager.getAllAccounts()).thenReturn(expectedAccounts);

        final List<Account> accounts = controller.accountSummary();

        Assert.assertNotNull("returned account list", accounts);
        Assert.assertEquals("account 0 entityId", Integer.valueOf(ACCOUNT0_ID),
                            accounts.get(ACCOUNT0_ID).getEntityId());
        Assert.assertEquals("account 1 entityId", Integer.valueOf(ACCOUNT1_ID),
                            accounts.get(ACCOUNT1_ID).getEntityId());
        Assert.assertEquals("accounts", expectedAccounts, accounts);

        verify(accountManager, times(1)).getAllAccounts();
        verifyNoMoreInteractions(accountManager);
    }


    /**
     * Test account details (single object)
     */
    @Test
    public void testAccountDetails() {
        final Account account0 = createAccount0();
        account0.addBeneficiary(TEST_BENEFICIARY_0_0, PERCENTAGE_50PC);
        account0.addBeneficiary(TEST_BENEFICIARY_0_1, PERCENTAGE_50PC);

        when(accountManager.getAccount(ACCOUNT0_ID)).thenReturn(account0);

        final Account account = controller.accountDetails(ACCOUNT0_ID);

        Assert.assertNotNull("returned account", account);
        Assert.assertEquals("entityId", Integer.valueOf(ACCOUNT0_ID), account.getEntityId());
        Assert.assertEquals("account attributes", account0, account);

        verify(accountManager, times(1)).getAccount(ACCOUNT0_ID);
        verifyNoMoreInteractions(accountManager);
    }

    /**
     * Test createAccount
     */
    @Test
    @Ignore
    public void testCreateAccount() {
        final String accountsUrl = "http://testhost:8080/rewards/rest/accounts";
        final Account account = createAccount0();
        account.setEntityId(null);

        doAnswer(new Answer<Account>() {
            @Override
            public Account answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Account newAccount = (Account) invocationOnMock.getArguments()[0];
                newAccount.setEntityId(ACCOUNT0_ID);
                return newAccount;
            }

        }).when(accountManager).create(account);

        final ResponseEntity<Object> response = controller.createAccount(account, accountsUrl);

        // TODO 07: Check returned ResponseEntity, remove @Ignore above

        verify(accountManager, times(1)).create(argThat(new AccountEqualsWithId(account)));
        verifyNoMoreInteractions(accountManager);
    }


    /**
     * Test getBeneficiary
     */
    @Test
    @Ignore
    public void testGetBeneficiary() {
        final Account account0 = createAccount0();
        account0.addBeneficiary(TEST_BENEFICIARY_0_0, PERCENTAGE_50PC);
        final Beneficiary expectedBeneficiary0 = account0.getBeneficiary(TEST_BENEFICIARY_0_0);

        when(accountManager.getAccount(ACCOUNT0_ID)).thenReturn(account0);

        // TODO 10: Modify to verify returned beneficiary, remove @Ignore above

        final Beneficiary beneficiary = controller.getBeneficiary(ACCOUNT0_ID, TEST_BENEFICIARY_0_0);
        Assert.assertNotNull("returned beneficiary", beneficiary);
        Assert.assertEquals("beneficiary 0", expectedBeneficiary0, beneficiary);

        verify(accountManager, times(1)).getAccount(ACCOUNT0_ID);
        verifyNoMoreInteractions(accountManager);
    }


    /**
     * Test addBeneficiary
     */
    @Test
    @Ignore
    public void testAddBeneficiary() throws Exception {
        final int accountId = 0;
        final String beneficiariesUrl = "http://testhost:8080/rewards/rest/accounts/" + accountId + "/beneficiaries";
        final String beneficiaryName = "TestBeneficiary0-0";

        final ResponseEntity<Object> response = controller.addBeneficiary(accountId, beneficiaryName, beneficiariesUrl);

        // TODO 13: Ensure requestURL is used to create appropriate Location header

        verify(accountManager, times(1)).addAccountBeneficiary(accountId, beneficiaryName);
        verifyNoMoreInteractions(accountManager);
    }


    /**
     * Test removeBeneficiary
     */
    @Test
    @Ignore  //TODO 15: Remove @Ignore
    public void testRemoveBeneficiary() {
        final String beneficiariesUrl =
                "http://testhost:8080/rewards/rest/accounts/" + ACCOUNT0_ID + "/beneficiaries/" + TEST_BENEFICIARY_0_0;

        controller.removeBeneficiary(ACCOUNT0_ID, TEST_BENEFICIARY_0_0);

        verify(accountManager, times(1)).removeAccountBeneficiary(ACCOUNT0_ID, TEST_BENEFICIARY_0_0);
        verifyNoMoreInteractions(accountManager);
    }


    /**
     * Test handle EmptyResultDataAccessException
     */
    @Test
    @Ignore  //TODO 19: Remove @Ignore
    public void testHandleNotFound() {
        final EmptyResultDataAccessException ex = new EmptyResultDataAccessException("not found", 1);
        final String actualMsg = controller.handleNotFound(ex);

        Assert.assertEquals("exception message should be returned" + ex, ex.getMessage(), actualMsg);

        verifyZeroInteractions(accountManager);
    }


    // TODO 22: Add new test for handling DataIntegrityViolationException


    /**
     * Helper creating account 0
     *
     * @return account
     */
    private Account createAccount0() {
        Account account0 =
                new Account("123457890", "Test Account 0",
                            new SimpleDate(10, 10, 1980).asDate(), "test@gmail.com",
                            true, false,
                            new CreditCard("4500123456789000"),
                            new CreditCard("3600123456789001"));
        account0.setEntityId(0);
        return account0;
    }



    /**
     * Argument matcher for accountInfo including entityId and equals
     */
    public class AccountEqualsWithId extends ArgumentMatcher<Account> {

        private final Account wantedAccount;

        private AccountEqualsWithId(Account wantedAccount) {
            this.wantedAccount = wantedAccount;
        }


        @Override
        public boolean matches(Object o) {
            boolean isEqual = wantedAccount.equals(o);
            if (isEqual) {
                final Account suppliedAccount = (Account) o;
                final Integer wantedEntityId = wantedAccount.getEntityId();
                final Integer suppliedEntityId = suppliedAccount.getEntityId();
                isEqual = (wantedEntityId == null ? suppliedEntityId == null : wantedEntityId.equals(suppliedEntityId));
            }

            return isEqual;
        }
    }
}
