package rewards.internal.account;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Unit tests for the full account manager implementation {@link AccountManagerImpl}.
 */
public class AccountManagerImplTests {

    private AccountManagerImpl accountManager;

    private AccountRepository accountRepository;

    /**
     * Set up session factory
     */
    @Before
    public void setUp() throws Exception {
        // setup the repository to test
        accountRepository = EasyMock.createMock(AccountRepository.class);
        accountManager = new AccountManagerImpl(accountRepository);
    }

    /**
     * Verify we get all expected accounts
     */
    @Test
    public void testGetAllAccounts() {
        Account account = AccountTestData.createTestAccount0();
        EasyMock.expect(accountRepository.getAllAccounts()).andReturn(Collections.singletonList(account));
        EasyMock.replay(accountRepository);

        List<Account> accounts = accountManager.getAllAccounts();
        Assert.assertEquals("Wrong number of accounts", 1, accounts.size());

        EasyMock.verify(accountRepository);
    }

    /**
     * Test getAccount
     */
    @Test
    public void testGetAccount() {
        Account account0 = AccountTestData.createTestAccount0();
        EasyMock.expect(accountRepository.getAccount(0)).andReturn(account0);
        EasyMock.replay(accountRepository);

        Account account = accountManager.getAccount(0);
        // assert the returned account contains what you expect given the state
        // of the database
        Assert.assertNotNull("account should never be null", account);
        AccountTestData.verifyAccount0(account, 2);

        AccountTestData.verifyBeneficiary(account,
                                          AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                          AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                          AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

        AccountTestData.verifyBeneficiary(account,
                                          AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                                          AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                                          AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);

        EasyMock.verify(accountRepository);
    }

    /**
     * Test getAccount for non-existent account
     */
    @Test
    public void testGetNonExistentAccount() {
        EasyMock.expect(accountRepository.getAccount(1)).andReturn(null);
        EasyMock.replay(accountRepository);

        try {
            Account account = accountManager.getAccount(1);
            Assert.fail("expected empty data result exception account=" + account);
        } catch (EmptyResultDataAccessException ex) {
            String exmsg = ex.getMessage();
            Assert.assertTrue("exception msg should include account id:" + exmsg,
                              exmsg.contains("id=" + 1));
            EasyMock.verify(accountRepository);
        }
    }

    /**
     * Test updateAccountInfo, in particular, check that beneficiaries are preserved by
     * updateAccountInfo of account attributes
     */
    @Test
    public void testUpdateAccount() {
        Account storedAccount = AccountTestData.createTestAccount0();
        Account account = AccountTestData.createTestAccount0();
        account.setName("Ben Hale");
        account.getBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0)
               .setAllocationPercentage(AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_1);

        account.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_2,
                               AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_2);
        account.getBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_2)
               .credit(AccountTestData.TEST_BENEFICIARY_SAVINGS_1_2);
        account.removeBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_1);

        EasyMock.expect(accountRepository.getAccount(0)).andReturn(storedAccount);
        EasyMock.expect(accountRepository.hasNoAccountWithName("Ben Hale")).andReturn(true);
        accountRepository.update(account);
        EasyMock.replay(accountRepository);

        accountManager.update(account);

        AccountTestData.verifyAccount(account, 0, AccountTestData.TEST_NUMBER_0,
                                      "Ben Hale",
                                      AccountTestData.TEST_DOB_0,
                                      AccountTestData.TEST_EMAIL_0,
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0, 2,
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));

        AccountTestData.verifyBeneficiary(account,
                                          AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                          AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_1,
                                          AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

        AccountTestData.verifyBeneficiary(account,
                                          AccountTestData.TEST_BENEFICIARY_NAME_1_2,
                                          AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_2,
                                          AccountTestData.TEST_BENEFICIARY_SAVINGS_1_2);

        EasyMock.verify(accountRepository);
    }

    /**
     * Test update for non-existent account
     */
    @Test
    public void testUpdateAccountNonExistentAccount() {
        Account account = AccountTestData.createTestAccount1();
        account.setEntityId(999);
        account.setName(AccountTestData.TEST_NAME_0);

        EasyMock.expect(accountRepository.getAccount(999)).andReturn(null);
        EasyMock.replay(accountRepository);

        try {
            accountManager.update(account);
            Assert.fail("expected empty data result exception account=" + account);
        } catch (EmptyResultDataAccessException ex) {
            String exmsg = ex.getMessage();
            Assert.assertTrue("exception msg should include account id:" + exmsg,
                              exmsg.contains("id=" + 999));
            EasyMock.verify(accountRepository);
        }
    }

    /**
     * Test update with duplicate name
     */
    @Test
    public void testUpdateAccountDuplicateName() {
        Account storedAccount = AccountTestData.createTestAccount1();
        storedAccount.setEntityId(1);
        Account account = AccountTestData.createTestAccount1();
        account.setEntityId(1);
        account.setName(AccountTestData.TEST_NAME_0);

        EasyMock.expect(accountRepository.getAccount(1)).andReturn(storedAccount);
        EasyMock.expect(accountRepository.hasNoAccountWithName(AccountTestData.TEST_NAME_0))
                .andReturn(false);
        EasyMock.replay(accountRepository);

        try {
            accountManager.update(account);
            Assert.fail("expected duplicate name exception for account=" + account);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate name, but not duplicate number or duplicate credit cards:" + ex,
                              ex.hasDuplicateAccountName() &&
                              !(ex.hasDuplicateAccountNumber() || ex.hasDuplicateCreditCardNumbers()));
            Assert.assertEquals("duplicate name", AccountTestData.TEST_NAME_0, ex.getDuplicateAccountName());

            EasyMock.verify(accountRepository);
        }
    }

    /**
     * Test update with duplicate credit cards
     */
    @Test
    public void testUpdateAccountDuplicateCreditCards() {
        Account storedAccount = AccountTestData.createTestAccount1();
        storedAccount.setEntityId(1);

        Account account = AccountTestData.createTestAccount1();
        account.setEntityId(1);
        account.addCreditCard(new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));

        EasyMock.expect(accountRepository.getAccount(1)).andReturn(storedAccount);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_0))
                .andReturn(false);
        EasyMock.replay(accountRepository);

        try {
            accountManager.update(account);
            Assert.fail("expected duplicate credit card exception for account=" + account);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate credit cards, but not duplicate number or duplicate name:" + ex,
                              ex.hasDuplicateCreditCardNumbers() &&
                              !(ex.hasDuplicateAccountNumber() || ex.hasDuplicateAccountName()));
            Assert.assertEquals("duplicate creditCards", Collections.singleton(AccountTestData.TEST_CREDIT_CARD_0),
                                ex.getDuplicateCreditCardNumbers());

            EasyMock.verify(accountRepository);
        }
    }

    /**
     * Test update with duplicate name and credit cards
     */
    @Test
    public void testUpdateAccountDuplicateNameAndCreditCards() {
        Account storedAccount = AccountTestData.createTestAccount1();
        storedAccount.setEntityId(1);
        Account account = AccountTestData.createTestAccount1();
        account.setEntityId(1);
        account.setName(AccountTestData.TEST_NAME_0);
        account.addCreditCard(new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));

        EasyMock.expect(accountRepository.getAccount(1)).andReturn(storedAccount);
        EasyMock.expect(accountRepository.hasNoAccountWithName(AccountTestData.TEST_NAME_0))
                .andReturn(false);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_0))
                .andReturn(false);
        EasyMock.replay(accountRepository);

        try {
            accountManager.update(account);
            Assert.fail("expected duplicate name and credit card exception for account=" + account);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate name and credit cards, but not duplicate number:" + ex,
                              ex.hasDuplicateCreditCardNumbers() && ex.hasDuplicateAccountName() &&
                              !ex.hasDuplicateAccountNumber());
            Assert.assertEquals("duplicate name", AccountTestData.TEST_NAME_0, ex.getDuplicateAccountName());
            Assert.assertEquals("duplicate creditCards", Collections.singleton(AccountTestData.TEST_CREDIT_CARD_0),
                                ex.getDuplicateCreditCardNumbers());

            EasyMock.verify(accountRepository);
        }
    }

    /**
     * Verify create from original account object
     */
    @Test
    public void testCreateAccount() {
        Account account = AccountTestData.createTestAccount0();
        EasyMock.expect(accountRepository.hasNoAccountWithName(AccountTestData.TEST_NAME_0)).andReturn
                (true);
        EasyMock.expect(accountRepository.hasNoAccountWithNumber(AccountTestData.TEST_NUMBER_0))
                .andReturn(true);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_0))
                .andReturn(true);
        accountRepository.create(account);
        EasyMock.replay(accountRepository);

        accountManager.create(account);

        Assert.assertNotNull("created account", account);

        AccountTestData.verifyAccount0(account, 2);
        AccountTestData.verifyBeneficiary(account,
                                          AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                          AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                          AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

        AccountTestData.verifyBeneficiary(account,
                                          AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                                          AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                                          AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);

        EasyMock.verify(accountRepository);
    }

    /**
     * Verify create from original object with duplicate name
     */
    @Test
    public void testCreateAccountDuplicateName() {
        Account account = AccountTestData.createTestAccount1();
        account.setName(AccountTestData.TEST_NAME_0);
        EasyMock.expect(accountRepository.hasNoAccountWithName(AccountTestData.TEST_NAME_0))
                .andReturn(false);
        EasyMock.expect(accountRepository.hasNoAccountWithNumber(AccountTestData.TEST_NUMBER_1)).andReturn(
                true);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_1))
                .andReturn(true);
        EasyMock.replay(accountRepository);

        try {
            accountManager.create(account);
            Assert.fail("expected duplicate name for account="
                        + account);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate name, but not duplicate number or credit cards:" + ex,
                              ex.hasDuplicateAccountName() &&
                              !(ex.hasDuplicateAccountNumber() || ex.hasDuplicateCreditCardNumbers()));
            Assert.assertEquals("duplicate name", AccountTestData.TEST_NAME_0, ex.getDuplicateAccountName());

            EasyMock.verify(accountRepository);
        }
    }

    /**
     * Verify create from original object with duplicate number
     */
    @Test
    public void testCreateAccountDuplicateNumber() {
        Account account = AccountTestData.createTestAccount1();
        account.setNumber(AccountTestData.TEST_NUMBER_0);
        EasyMock.expect(accountRepository.hasNoAccountWithName(AccountTestData.TEST_NAME_1)).andReturn
                (true);
        EasyMock.expect(accountRepository.hasNoAccountWithNumber(AccountTestData.TEST_NUMBER_0))
                .andReturn(false);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_1))
                .andReturn(true);
        EasyMock.replay(accountRepository);

        try {
            accountManager.create(account);
            Assert.fail("expected duplicate number for account="
                        + account);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate number, but not duplicate name or credit cards:" + ex,
                              ex.hasDuplicateAccountNumber() &&
                              !(ex.hasDuplicateAccountName() || ex.hasDuplicateCreditCardNumbers()));
            Assert.assertEquals("duplicate number", AccountTestData.TEST_NUMBER_0, ex.getDuplicateAccountNumber());

            EasyMock.verify(accountRepository);
        }
    }

    /**
     * Verify create from original object with duplicate credit card
     */
    @Test
    public void testCreateAccountDuplicateCreditCards1Duplicate() {
        Account account = AccountTestData.createTestAccount1();
        account.addCreditCard(new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));
        EasyMock.expect(accountRepository.hasNoAccountWithName(AccountTestData.TEST_NAME_1)).andReturn
                (true);
        EasyMock.expect(accountRepository.hasNoAccountWithNumber(AccountTestData.TEST_NUMBER_1))
                .andReturn(true);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_0))
                .andReturn(false);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_1))
                .andReturn(true);
        EasyMock.replay(accountRepository);

        try {
            accountManager.create(account);
            Assert.fail("expected duplicate credit cards for account="
                        + account);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate credit cards, but not duplicate name or number:" + ex,
                              ex.hasDuplicateCreditCardNumbers() &&
                              !(ex.hasDuplicateAccountName() || ex.hasDuplicateAccountNumber()));
            Assert.assertEquals("duplicate credit cards", Collections.singleton(AccountTestData.TEST_CREDIT_CARD_0),
                                ex.getDuplicateCreditCardNumbers());

            EasyMock.verify(accountRepository);
        }
    }

    /**
     * Verify create from original object with two duplicate credit cards
     */
    @Test
    public void testCreateAccountDuplicateCreditCards2Duplicates() {
        Account account = AccountTestData.createTestAccount1();
        account.addCreditCard(new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));
        account.addCreditCard(new CreditCard(AccountTestData.TEST_CREDIT_CARD_UNUSED));
        EasyMock.expect(accountRepository.hasNoAccountWithName(AccountTestData.TEST_NAME_1)).andReturn
                (true);
        EasyMock.expect(accountRepository.hasNoAccountWithNumber(AccountTestData.TEST_NUMBER_1))
                .andReturn(true);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_0))
                .andReturn(false);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_1))
                .andReturn(true);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_UNUSED))
                .andReturn(false);
        EasyMock.replay(accountRepository);

        try {
            accountManager.create(account);
            Assert.fail("expected duplicate credit cards for account="
                        + account);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate credit cards, but not duplicate name or number:" + ex,
                              ex.hasDuplicateCreditCardNumbers() &&
                              !(ex.hasDuplicateAccountName() || ex.hasDuplicateAccountNumber()));

            Set<String> expectedDuplicateCardNames = new HashSet<String>();
            expectedDuplicateCardNames.add(AccountTestData.TEST_CREDIT_CARD_0);
            expectedDuplicateCardNames.add(AccountTestData.TEST_CREDIT_CARD_UNUSED);

            Assert.assertEquals("duplicate numbers", expectedDuplicateCardNames, ex.getDuplicateCreditCardNumbers());

            EasyMock.verify(accountRepository);
        }
    }

    /**
     * Verify create from original object with duplicate name and number
     */
    @Test
    public void testCreateAccountDuplicateNameAndNumber() {
        Account account = AccountTestData.createTestAccount1();
        account.setName(AccountTestData.TEST_FIRST_DUPLICATE_NAME);
        account.setNumber(AccountTestData.TEST_NUMBER_0);
        EasyMock.expect(accountRepository.hasNoAccountWithName(AccountTestData.TEST_FIRST_DUPLICATE_NAME))
                .andReturn(false);
        EasyMock.expect(accountRepository.hasNoAccountWithNumber(AccountTestData.TEST_NUMBER_0)).andReturn(
                false);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_1))
                .andReturn(true);
        EasyMock.replay(accountRepository);

        try {
            accountManager.create(account);
            Assert.fail("expected duplicate name and number for account="
                        + account);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate number and duplicate name, but not duplicate credit cards:" + ex,
                              ex.hasDuplicateAccountNumber() && ex.hasDuplicateAccountName() &&
                              !ex.hasDuplicateCreditCardNumbers());
            Assert.assertEquals("duplicate name", AccountTestData.TEST_FIRST_DUPLICATE_NAME,
                                ex.getDuplicateAccountName());
            Assert.assertEquals("duplicate number", AccountTestData.TEST_NUMBER_0, ex.getDuplicateAccountNumber());

            EasyMock.verify(accountRepository);
        }
    }


    /**
     * Verify create from original object with duplicate name and credit card
     */
    @Test
    public void testCreateAccountDuplicateNameAndCreditCard() {
        Account account = AccountTestData.createTestAccount1();
        account.setName(AccountTestData.TEST_FIRST_DUPLICATE_NAME);
        account.addCreditCard(new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));
        EasyMock.expect(accountRepository.hasNoAccountWithName(AccountTestData.TEST_FIRST_DUPLICATE_NAME))
                .andReturn(false);
        EasyMock.expect(accountRepository.hasNoAccountWithNumber(AccountTestData.TEST_NUMBER_1)).andReturn(
                true);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_0))
                .andReturn(false);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_1))
                .andReturn(true);
        EasyMock.replay(accountRepository);

        try {
            accountManager.create(account);
            Assert.fail("expected duplicate name and credit card for account="
                        + account);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate name and duplicate credit cards, but not duplicate number:" + ex,
                              ex.hasDuplicateAccountName() && ex.hasDuplicateCreditCardNumbers() &&
                              !ex.hasDuplicateAccountNumber());
            Assert.assertEquals("duplicate name", AccountTestData.TEST_FIRST_DUPLICATE_NAME,
                                ex.getDuplicateAccountName());
            Assert.assertEquals("duplicate credit card numbers",
                                Collections.singleton(AccountTestData.TEST_CREDIT_CARD_0),
                                ex.getDuplicateCreditCardNumbers());

            EasyMock.verify(accountRepository);
        }
    }


    /**
     * Verify create from original object with duplicate number and credit card
     */
    @Test
    public void testCreateAccountDuplicateNumberAndCreditCard() {
        Account account = AccountTestData.createTestAccount1();
        account.setNumber(AccountTestData.TEST_NUMBER_0);
        account.addCreditCard(new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));
        EasyMock.expect(accountRepository.hasNoAccountWithName(AccountTestData.TEST_NAME_1))
                .andReturn(true);
        EasyMock.expect(accountRepository.hasNoAccountWithNumber(AccountTestData.TEST_NUMBER_0)).andReturn(
                false);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_0))
                .andReturn(false);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_1))
                .andReturn(true);
        EasyMock.replay(accountRepository);

        try {
            accountManager.create(account);
            Assert.fail("expected duplicate number and credit card for account="
                        + account);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate number and credit cards, but not name:" + ex,
                              ex.hasDuplicateAccountNumber() && ex.hasDuplicateCreditCardNumbers() &&
                              !ex.hasDuplicateAccountName());
            Assert.assertEquals("duplicate number", AccountTestData.TEST_NUMBER_0,
                                ex.getDuplicateAccountNumber());
            Assert.assertEquals("duplicate credit card numbers",
                                Collections.singleton(AccountTestData.TEST_CREDIT_CARD_0),
                                ex.getDuplicateCreditCardNumbers());

            EasyMock.verify(accountRepository);
        }
    }


    /**
     * Verify create from original object with duplicate name, number and credit card
     */
    @Test
    public void testCreateAccountDuplicateNameNumberAndCreditCard() {
        Account account = AccountTestData.createTestAccount1();
        account.setName(AccountTestData.TEST_FIRST_DUPLICATE_NAME);
        account.setNumber(AccountTestData.TEST_NUMBER_0);
        account.addCreditCard(new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));
        EasyMock.expect(accountRepository.hasNoAccountWithName(AccountTestData.TEST_FIRST_DUPLICATE_NAME))
                .andReturn(false);
        EasyMock.expect(accountRepository.hasNoAccountWithNumber(AccountTestData.TEST_NUMBER_0)).andReturn(
                false);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_0))
                .andReturn(false);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_1))
                .andReturn(true);
        EasyMock.replay(accountRepository);

        try {
            accountManager.create(account);
            Assert.fail("expected duplicate name and credit card for account="
                        + account);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate number, name, and credit cards:" + ex,
                              ex.hasDuplicateAccountNumber() && ex.hasDuplicateAccountName() &&
                              ex.hasDuplicateCreditCardNumbers());
            Assert.assertEquals("duplicate name", AccountTestData.TEST_FIRST_DUPLICATE_NAME,
                                ex.getDuplicateAccountName());
            Assert.assertEquals("duplicate number", AccountTestData.TEST_NUMBER_0,
                                ex.getDuplicateAccountNumber());
            Assert.assertEquals("duplicate credit card numbers",
                                Collections.singleton(AccountTestData.TEST_CREDIT_CARD_0),
                                ex.getDuplicateCreditCardNumbers());

            EasyMock.verify(accountRepository);
        }
    }


    /**
     * Verify create from original object with invalid beneficiary allocations (gt 100p%)
     */
    @Test
    public void testCreateAccountBeneficiaryAllocationsGt100pc() {
        Account account = AccountTestData.createTestAccount1();

        account.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_0,
                               AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_0);
        account.getBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_0)
               .credit(AccountTestData.TEST_BENEFICIARY_SAVINGS_1_0);
        account.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_1,
                               AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0);
        account.getBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_1)
               .credit(AccountTestData.TEST_BENEFICIARY_SAVINGS_1_1);
        account.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_2,
                               AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1);
        account.getBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_2)
               .credit(AccountTestData.TEST_BENEFICIARY_SAVINGS_1_2);

        EasyMock.expect(accountRepository.hasNoAccountWithNumber(AccountTestData.TEST_NUMBER_1)).andReturn(
                true);
        EasyMock.expect(accountRepository.hasNoAccountWithName(AccountTestData.TEST_NAME_1))
                .andReturn(true);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_1))
                .andReturn(true);
        EasyMock.replay(accountRepository);

        try {
            accountManager.create(account);
            Assert.fail("expected invalid beneficiary allocations for account="
                        + account);
        } catch (InvalidBeneficiaryAllocationsException ex) {
            final String expectedMsgContent = "total allocations gt 100%";
            Assert.assertTrue("exception message should include diagnostic '" + expectedMsgContent + "'",
                              ex.getMessage().contains(expectedMsgContent));

            EasyMock.verify(accountRepository);
        }
    }


    /**
     * Test addBeneficiary (one)
     */
    @Test
    public void testAddBeneficiary0() {
        Account account = AccountTestData.createTestAccount0();
        Account expectedAccount = AccountTestData.createTestAccount0();
        expectedAccount.addBeneficiary("Georgina");

        EasyMock.expect(accountRepository.getAccount(0)).andReturn(account);
        accountRepository.update(account);
        EasyMock.replay(accountRepository);

        accountManager.addAccountBeneficiary(0, "Georgina");

        Assert.assertEquals("account and beneficiaries", expectedAccount, account);

        EasyMock.verify(accountRepository);
    }


    /**
     * Test addBeneficiary (twice)
     */
    @Test
    public void testAddBeneficiary0And1() {
        Account account = AccountTestData.createTestAccount0();
        Account expectedAccount = AccountTestData.createTestAccount0();
        expectedAccount.addBeneficiary("Georgina");
        expectedAccount.addBeneficiary("Tristan");

        EasyMock.expect(accountRepository.getAccount(0)).andReturn(account).times(2);
        accountRepository.update(account);
        EasyMock.expectLastCall().times(2);
        EasyMock.replay(accountRepository);

        accountManager.addAccountBeneficiary(0, "Georgina");
        accountManager.addAccountBeneficiary(0, "Tristan");

        Assert.assertEquals("account and beneficiaries", expectedAccount, account);

        EasyMock.verify(accountRepository);
    }


    /**
     * Test addBeneficiary (duplicate)
     */
    @Test
    public void testAddBeneficiaryDuplicateName() {
        Account account = AccountTestData.createTestAccount0();

        EasyMock.expect(accountRepository.getAccount(0)).andReturn(account);
        EasyMock.replay(accountRepository);

        try {
            accountManager.addAccountBeneficiary(0, AccountTestData.TEST_BENEFICIARY_NAME_0_0);
            Assert.fail("expected empty data result exception account=" + account);
        } catch (DuplicateBeneficiaryNameException ex) {
            String exmsg = ex.getMessage();
            Assert.assertTrue("exception msg should include beneficiary name:" + exmsg,
                              exmsg.contains("beneficiaryName='" + AccountTestData.TEST_BENEFICIARY_NAME_0_0 + "'"));
            EasyMock.verify(accountRepository);
        }

        EasyMock.verify(accountRepository);
    }


    /**
     * Test removeBeneficiary
     */
    @Test
    public void testRemoveBeneficiary() {
        Account account = AccountTestData.createTestAccount0();
        Account expectedAccount = AccountTestData.createTestAccount0();
        expectedAccount.removeBeneficiary("Corgan");

        EasyMock.expect(accountRepository.getAccount(0)).andReturn(account);
        accountRepository.update(account);
        EasyMock.replay(accountRepository);

        accountManager.removeAccountBeneficiary(0, "Corgan");

        Assert.assertEquals("account and beneficiaries", expectedAccount, account);

        EasyMock.verify(accountRepository);
    }


    /**
     * Test getAccountInfo
     */
    @Test
    public void testGetAccountInfo() {
        Account account = AccountTestData.createTestAccount0();
        EasyMock.expect(accountRepository.getAccount(0)).andReturn(account);
        EasyMock.replay(accountRepository);

        AccountInfo accountInfo = accountManager.getAccountInfo(0);
        // assert the returned account contains what you expect given the state
        // of the database
        Assert.assertNotNull("accountInfo should never be null", accountInfo);
        AccountTestData.verifyAccountInfo0(accountInfo);
    }

    /**
     * Test getAccount for non-existent account
     */
    @Test
    public void testGetAccountInfoNonExistentAccount() {
        EasyMock.expect(accountRepository.getAccount(1)).andReturn(null);
        EasyMock.replay(accountRepository);

        try {
            AccountInfo accountInfo = accountManager.getAccountInfo(1);
            Assert.fail("expected empty data result exception accountInfo=" + accountInfo);
        } catch (EmptyResultDataAccessException ex) {
            String exmsg = ex.getMessage();
            Assert.assertTrue("exception msg should include account id:" + exmsg,
                              exmsg.contains("id=" + 1));
            EasyMock.verify(accountRepository);
        }
    }

    /**
     * Test updateAccountInfo, in particular, check that beneficiaries are preserved by
     * updateAccountInfo of account attributes
     */
    @Test
    public void testUpdateAccountInfo() {
        Account account = AccountTestData.createTestAccount0();
        AccountInfo accountInfo = new AccountInfo(account);
        accountInfo.setName("Ben Hale");
        EasyMock.expect(accountRepository.getAccount(0)).andReturn(account);
        EasyMock.expect(accountRepository.hasNoAccountWithName("Ben Hale")).andReturn(true);
        accountRepository.update(account);
        EasyMock.replay(accountRepository);

        accountManager.updateAccountInfo(accountInfo);

        AccountTestData.verifyAccount(account, new Integer(0),
                                      AccountTestData.TEST_NUMBER_0,
                                      "Ben Hale",
                                      AccountTestData.TEST_DOB_0,
                                      AccountTestData.TEST_EMAIL_0,
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0, 2,
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));

        AccountTestData.verifyBeneficiary(account,
                                          AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                          AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                          AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

        AccountTestData.verifyBeneficiary(account,
                                          AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                                          AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                                          AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);

        EasyMock.verify(accountRepository);
    }

    /**
     * Test updateAccountInfo, in particular, check that beneficiaries are preserved by
     * updateAccountInfo of account attributes
     */
    @Test
    public void testUpdateAccountInfoSameName() {
        Account account = AccountTestData.createTestAccount0();
        AccountInfo accountInfo = new AccountInfo(account);
        accountInfo.setEmail("keith.c@redblack-it.com");
        EasyMock.expect(accountRepository.getAccount(0)).andReturn(account);
        accountRepository.update(account);
        EasyMock.replay(accountRepository);

        accountManager.updateAccountInfo(accountInfo);

        AccountTestData.verifyAccount(account, new Integer(0),
                                      AccountTestData.TEST_NUMBER_0,
                                      AccountTestData.TEST_NAME_0,
                                      AccountTestData.TEST_DOB_0,
                                      "keith.c@redblack-it.com",
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0, 2,
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));

        AccountTestData.verifyBeneficiary(account,
                                          AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                          AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                          AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

        AccountTestData.verifyBeneficiary(account,
                                          AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                                          AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                                          AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);

        EasyMock.verify(accountRepository);
    }

    /**
     * Test updateAccountInfo for non-existent account
     */
    @Test
    public void testUpdateAccountInfoNonExistentAccount() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo1();
        accountInfo.setEntityId(1);
        accountInfo.setName(AccountTestData.TEST_NAME_0);

        EasyMock.expect(accountRepository.getAccount(1)).andReturn(null);
        EasyMock.replay(accountRepository);

        try {
            accountManager.updateAccountInfo(accountInfo);
            Assert.fail("expected empty data result exception accountInfo=" + accountInfo);
        } catch (EmptyResultDataAccessException ex) {
            String exmsg = ex.getMessage();
            Assert.assertTrue("exception msg should include account id:" + exmsg,
                              exmsg.contains("id=" + 1));
            EasyMock.verify(accountRepository);
        }
    }

    /**
     * Verify updateAccountInfo with duplicate name
     */
    @Test
    public void testUpdateAccountInfoDuplicateName() {
        Account account = AccountTestData.createTestAccount1();
        account.setEntityId(1);
        AccountInfo accountInfo = new AccountInfo(account);
        accountInfo.setName(AccountTestData.TEST_NAME_0);

        EasyMock.expect(accountRepository.getAccount(1)).andReturn(account);
        EasyMock.expect(accountRepository.hasNoAccountWithName(AccountTestData.TEST_NAME_0))
                .andReturn(false);
        EasyMock.replay(accountRepository);

        try {
            accountManager.updateAccountInfo(accountInfo);
            Assert.fail("expected duplicate name for accountInfo="
                        + accountInfo);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("has duplicate name", ex.hasDuplicateAccountName());
            Assert.assertFalse("no duplicate number", ex.hasDuplicateAccountNumber());
            Assert.assertFalse("no duplicate credit cards", ex.hasDuplicateCreditCardNumbers());
            Assert.assertEquals("duplicate name", AccountTestData.TEST_NAME_0, ex.getDuplicateAccountName());

            EasyMock.verify(accountRepository);
        }
    }

    /**
     * Verify createAccountInfo from DTO
     */
    @Test
    public void testCreateAccountInfo() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo1();
        Account account = AccountTestData.createTestAccount1();

        EasyMock.expect(accountRepository.hasNoAccountWithName(AccountTestData.TEST_NAME_1)).andReturn
                (true);
        EasyMock.expect(accountRepository.hasNoAccountWithNumber(AccountTestData.TEST_NUMBER_1))
                .andReturn(true);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_1))
                .andReturn(true);
        accountRepository.create(account);
        EasyMock.replay(accountRepository);

        Account createdAccount = accountManager.createAccountInfo(accountInfo);

        Assert.assertNotNull("created account", createdAccount);
        AccountTestData.verifyAccount(createdAccount, null,
                                      AccountTestData.TEST_NUMBER_1,
                                      AccountTestData.TEST_NAME_1,
                                      AccountTestData.TEST_DOB_1,
                                      AccountTestData.TEST_EMAIL_1,
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_1,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1, 0,
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_1));

        EasyMock.verify(accountRepository);
    }

    /**
     * Verify createAccountInfo from DTO with duplicate name
     */
    @Test
    public void testCreateAccountInfoDuplicateName() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo1();
        accountInfo.setName(AccountTestData.TEST_NAME_0);
        EasyMock.expect(accountRepository.hasNoAccountWithName(AccountTestData.TEST_NAME_0))
                .andReturn(false);
        EasyMock.expect(accountRepository.hasNoAccountWithNumber(AccountTestData.TEST_NUMBER_1))
                .andReturn(true);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_1))
                .andReturn(true);
        EasyMock.replay(accountRepository);

        try {
            accountManager.createAccountInfo(accountInfo);
            Assert.fail("expected duplicate name for accountInfo="
                        + accountInfo);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("has duplicate name", ex.hasDuplicateAccountName());
            Assert.assertFalse("no duplicate number", ex.hasDuplicateAccountNumber());
            Assert.assertFalse("no duplicate credit cards", ex.hasDuplicateCreditCardNumbers());
            Assert.assertEquals("duplicate name", AccountTestData.TEST_NAME_0, ex.getDuplicateAccountName());

            EasyMock.verify(accountRepository);
        }
    }

    /**
     * Verify createAccountInfo from DTO with duplicate number
     */
    @Test
    public void testCreateAccountInfoDuplicateNumber() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo1();
        accountInfo.setNumber(AccountTestData.TEST_NUMBER_0);
        EasyMock.expect(accountRepository.hasNoAccountWithName(AccountTestData.TEST_NAME_1)).andReturn
                (true);
        EasyMock.expect(accountRepository.hasNoAccountWithNumber(AccountTestData.TEST_NUMBER_0))
                .andReturn(false);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_1))
                .andReturn(true);
        EasyMock.replay(accountRepository);

        try {
            accountManager.createAccountInfo(accountInfo);
            Assert.fail("expected duplicate number for accountInfo="
                        + accountInfo);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertFalse("no duplicate name", ex.hasDuplicateAccountName());
            Assert.assertTrue("has duplicate number", ex.hasDuplicateAccountNumber());
            Assert.assertFalse("no duplicate credit cards", ex.hasDuplicateCreditCardNumbers());
            Assert.assertEquals("duplicate number", AccountTestData.TEST_NUMBER_0, ex.getDuplicateAccountNumber());

            EasyMock.verify(accountRepository);
        }
    }

    /**
     * Verify createAccountInfo from DTO with duplicate name and number where both are in one other
     * account
     */
    @Test
    public void testCreateAccountInfoDuplicateNameAndNumber() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo1();
        accountInfo.setName(AccountTestData.TEST_FIRST_DUPLICATE_NAME);
        accountInfo.setNumber(AccountTestData.TEST_NUMBER_0);
        EasyMock.expect(accountRepository.hasNoAccountWithName(AccountTestData.TEST_FIRST_DUPLICATE_NAME))
                .andReturn(false);
        EasyMock.expect(accountRepository.hasNoAccountWithNumber(AccountTestData.TEST_NUMBER_0))
                .andReturn(false);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_1))
                .andReturn(true);
        EasyMock.replay(accountRepository);

        try {
            accountManager.createAccountInfo(accountInfo);
            Assert.fail("expected duplicate name and number for accountInfo="
                        + accountInfo);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("has duplicate name", ex.hasDuplicateAccountName());
            Assert.assertTrue("has duplicate number", ex.hasDuplicateAccountNumber());
            Assert.assertFalse("no duplicate credit cards", ex.hasDuplicateCreditCardNumbers());
            Assert.assertEquals("duplicate number", AccountTestData.TEST_NUMBER_0, ex.getDuplicateAccountNumber());
            Assert.assertEquals("duplicate name", AccountTestData.TEST_NAME_0, ex.getDuplicateAccountName());

            EasyMock.verify(accountRepository);
        }
    }

    /**
     * Verify createAccountInfo from DTO with credit card that is already in use
     */
    @Test
    public void testCreateAccountInfoCreditCardInUse() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo1();
        accountInfo.removeCreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1);
        accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0));
        EasyMock.expect(accountRepository.hasNoAccountWithName(AccountTestData.TEST_NAME_1))
                .andReturn(true);
        EasyMock.expect(accountRepository.hasNoAccountWithNumber(AccountTestData.TEST_NUMBER_1))
                .andReturn(true);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(AccountTestData.TEST_CREDIT_CARD_0))
                .andReturn(false);
        EasyMock.replay(accountRepository);

        try {
            accountManager.createAccountInfo(accountInfo);
            Assert.fail("expected duplicate name and number for accountInfo="
                        + accountInfo);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertFalse("no duplicate name", ex.hasDuplicateAccountName());
            Assert.assertFalse("no duplicate number", ex.hasDuplicateAccountNumber());
            Assert.assertTrue("has duplicate credit cards", ex.hasDuplicateCreditCardNumbers());
            Assert.assertEquals("duplicate credit card numbers",
                                Collections.singleton(AccountTestData.TEST_CREDIT_CARD_0),
                                ex.getDuplicateCreditCardNumbers());

            EasyMock.verify(accountRepository);
        }
    }

    /**
     * Test hasNoAccountWithName for name that will not be there
     */
    @Test
    public void testIsAccountNameAvailableForCreateNameUnused() {
        final String nameToTest = AccountTestData.TEST_NAME_UNUSED;
        EasyMock.expect(accountRepository.hasNoAccountWithName(nameToTest)).andReturn(true);
        EasyMock.replay(accountRepository);

        Assert.assertTrue("should be available " + nameToTest,
                          accountManager.isAccountNameAvailableForCreate(nameToTest));

        EasyMock.verify(accountRepository);
    }

    /**
     * Test hasNoAccountWithName for name that will already be there
     */
    @Test
    public void testIsAccountNameAvailableForCreateNameInUse() {
        final String nameToTest = AccountTestData.TEST_NAME_0;
        EasyMock.expect(accountRepository.hasNoAccountWithName(nameToTest)).andReturn(false);
        EasyMock.replay(accountRepository);

        Assert.assertFalse("should not be available " + nameToTest,
                           accountManager.isAccountNameAvailableForCreate(nameToTest));

        EasyMock.verify(accountRepository);
    }

    /**
     * Test isAccountNameAvailableForUpdate for name that is free
     */
    @Test
    public void testIsAccountNameAvailableForUpdateNameUnused() {
        final Account account = AccountTestData.createTestAccount0();
        final String nameToTest = AccountTestData.TEST_NAME_UNUSED;
        final int idToTest = 0;
        EasyMock.expect(accountRepository.getAccount(idToTest)).andReturn(account);
        EasyMock.expect(accountRepository.hasNoAccountWithName(nameToTest)).andReturn(true);
        EasyMock.replay(accountRepository);

        Assert.assertTrue("should be available (unused) " + nameToTest + ":id=" + idToTest,
                          accountManager.isAccountNameAvailableForUpdate(idToTest, nameToTest));

        EasyMock.verify(accountRepository);
    }

    /**
     * Test isAccountNameAvailableForUpdate for name that is in use on different account
     */
    @Test
    public void testIsAccountNameAvailableForUpdateNameInUseForDifferentId() {
        final Account account = AccountTestData.createTestAccount0();
        final String nameToTest = AccountTestData.TEST_NAME_1;
        final int idToTest = 0;
        EasyMock.expect(accountRepository.getAccount(idToTest)).andReturn(account);
        EasyMock.expect(accountRepository.hasNoAccountWithName(nameToTest)).andReturn(false);
        EasyMock.replay(accountRepository);

        Assert.assertFalse("should not be available " + nameToTest + ":id=" + idToTest,
                           accountManager.isAccountNameAvailableForUpdate(idToTest, nameToTest));

        EasyMock.verify(accountRepository);
    }

    /**
     * Test isAccountNameAvailableForUpdate for name that is in use on same account
     */
    @Test
    public void testIsAccountNameAvailableForUpdateNameInUseForSameId() {
        final Account account = AccountTestData.createTestAccount0();
        final String nameToTest = AccountTestData.TEST_NAME_0;
        final int idToTest = 0;
        EasyMock.expect(accountRepository.getAccount(idToTest)).andReturn(account);
        EasyMock.replay(accountRepository);

        Assert.assertTrue("should be available " + nameToTest + ":id=" + idToTest,
                          accountManager.isAccountNameAvailableForUpdate(idToTest, nameToTest));

        EasyMock.verify(accountRepository);
    }

    /**
     * Test isAccountNameAvailableForUpdate for name in use and non-existent Id
     */
    @Test
    public void testIsAccountNameAvailableForUpdateNameInUseAndNonExistentId() {
        final String nameToTest = AccountTestData.TEST_NAME_0;
        final int idToTest = 999;
        EasyMock.expect(accountRepository.getAccount(idToTest)).andReturn(null);
        EasyMock.replay(accountRepository);

        try {
            accountManager.isAccountNameAvailableForUpdate(idToTest, nameToTest);
            Assert.fail("expecting EmptyResultDataAccessException for " + nameToTest + ":non-existent id=" + idToTest);
        } catch (EmptyResultDataAccessException ex) {
            EasyMock.verify(accountRepository);
        }
    }

    /**
     * Test hasNoAccountWithNumber for number that will be there
     */
    @Test
    public void testIsAccountNumberAvailableForCreateNumberUnused() {
        final String numberToTest = AccountTestData.TEST_NUMBER_UNUSED;
        EasyMock.expect(accountRepository.hasNoAccountWithNumber(numberToTest)).andReturn(true);
        EasyMock.replay(accountRepository);

        Assert.assertTrue("should be available " + numberToTest,
                          accountManager.isAccountNumberAvailableForCreate(numberToTest));

        EasyMock.verify(accountRepository);
    }

    /**
     * Test hasNoAccountWithNumber for number that will not be there
     */
    @Test
    public void testIsAccountNumberAvailableForCreateNumberInUse() {
        final String numberToTest = AccountTestData.TEST_NUMBER_0;
        EasyMock.expect(accountRepository.hasNoAccountWithNumber(numberToTest)).andReturn(false);
        EasyMock.replay(accountRepository);

        Assert.assertFalse("should not be available " + numberToTest,
                           accountManager.isAccountNumberAvailableForCreate(numberToTest));

        EasyMock.verify(accountRepository);
    }

    /**
     * Test getAccountBeneficiaryInfo
     */
    @Test
    public void testGetAccountBeneficiaryInfo() {
        Account account = AccountTestData.createTestAccount0();
        EasyMock.expect(accountRepository.getAccount(0)).andReturn(account);
        EasyMock.replay(accountRepository);

        AccountBeneficiaryInfo accountBeneficiaryInfo = accountManager
                .getAccountBeneficiaryInfo(0);

        AccountTestData.verifyAccountBeneficiaryInfo0(accountBeneficiaryInfo, 2);

        AccountTestData.verifyBeneficiaryInfo(accountBeneficiaryInfo,
                                              AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

        AccountTestData.verifyBeneficiaryInfo(accountBeneficiaryInfo,
                                              AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                                              AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                                              AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);

        EasyMock.verify(accountRepository);
    }

    /**
     * Test getAccountBeneficiaryInfo for non-existent account
     */
    @Test
    public void testGetAccountBeneficiaryInfoNonExistentAccount() {
        EasyMock.expect(accountRepository.getAccount(1)).andReturn(null);
        EasyMock.replay(accountRepository);

        try {
            AccountBeneficiaryInfo accountBeneficiaryInfo = accountManager.getAccountBeneficiaryInfo(1);
            Assert.fail("expected empty data result exception accountBeneficiaryInfo=" + accountBeneficiaryInfo);
        } catch (EmptyResultDataAccessException ex) {
            String exmsg = ex.getMessage();
            Assert.assertTrue("exception msg should include account id:" + exmsg,
                              exmsg.contains("id=" + 1));
            EasyMock.verify(accountRepository);
        }
    }

    /**
     * Test updateAccountBeneficiaries
     */
    @Test
    public void testUpdateAccountBeneficiaries() {
        Account account = AccountTestData.createTestAccount0();
        EasyMock.expect(accountRepository.getAccount(0)).andReturn(account);
        accountRepository.update(account);
        EasyMock.replay(accountRepository);

        AccountBeneficiaryInfo accountBeneficiaryInfo = new AccountBeneficiaryInfo(
                account);

        accountBeneficiaryInfo.getBeneficiary(
                AccountTestData.TEST_BENEFICIARY_NAME_0_0)
                              .setAllocationPercentage(
                                      AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_1);

        BeneficiaryInfo beneficiaryInfo2 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_1_2,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_2,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_1_2);

        accountBeneficiaryInfo
                .removeBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_1);
        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo2);

        accountManager.updateAccountBeneficiaries(accountBeneficiaryInfo);

        EasyMock.verify(accountRepository);
    }

    /**
     * Test updateAccountBeneficiaries for non-existent account
     */
    @Test
    public void testUpdateAccountBeneficiariesNonExistentAccount() {
        EasyMock.expect(accountRepository.getAccount(0)).andReturn(null);
        EasyMock.replay(accountRepository);

        AccountBeneficiaryInfo accountBeneficiaryInfo = AccountTestData.createTestAccountBeneficiaryInfo0();

        try {
            accountManager.updateAccountBeneficiaries(accountBeneficiaryInfo);
            Assert.fail("expected empty data result exception accountBeneficiaryInfo=" + accountBeneficiaryInfo);
        } catch (EmptyResultDataAccessException ex) {
            String exmsg = ex.getMessage();
            Assert.assertTrue("exception msg should include account id:" + exmsg,
                              exmsg.contains("id=" + 0));
            EasyMock.verify(accountRepository);
        }

    }

    /**
     * Test isCreditCardAvailableForCreateCard for card that is not in use
     */
    @Test
    public void testIsCreditCardAvailableForCreateCardUnused() {
        final String cardToTest = AccountTestData.TEST_CREDIT_CARD_UNUSED;
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(cardToTest)).andReturn(true);
        EasyMock.replay(accountRepository);

        Assert.assertTrue("should be available " + cardToTest,
                          accountManager.isCreditCardAvailableForAccountCreate(cardToTest));

        EasyMock.verify(accountRepository);
    }

    /**
     * Test hasNoAccountWithName for card that is in use
     */
    @Test
    public void testIsCreditCardAvailableForCreateCardInUse() {
        final String cardToTest = AccountTestData.TEST_CREDIT_CARD_0;
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(cardToTest)).andReturn(false);
        EasyMock.replay(accountRepository);

        Assert.assertFalse("should not be available " + cardToTest,
                           accountManager.isCreditCardAvailableForAccountCreate(cardToTest));

        EasyMock.verify(accountRepository);
    }

    /**
     * Test isCreditCardAvailableForUpdateCard for card that is not in use
     */
    @Test
    public void testIsCreditCardAvailableForUpdateCardUnused() {
        final Account account = AccountTestData.createTestAccount0();
        final String cardToTest = AccountTestData.TEST_CREDIT_CARD_UNUSED;
        final int idToTest = 0;
        EasyMock.expect(accountRepository.getAccount(0)).andReturn(account);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(cardToTest)).andReturn(true);
        EasyMock.replay(accountRepository);

        Assert.assertTrue("should be available " + cardToTest + ":idToTest=" + idToTest,
                          accountManager.isCreditCardAvailableForAccountUpdate(idToTest, cardToTest));

        EasyMock.verify(accountRepository);
    }

    /**
     * Test isCreditCardAvailableForUpdateCard for card that is use on same ID
     */
    @Test
    public void testIsCreditCardAvailableForUpdateCardInUseOnSameId() {
        final Account account = AccountTestData.createTestAccount0();
        final String cardToTest = AccountTestData.TEST_CREDIT_CARD_0;
        final int idToTest = 0;
        EasyMock.expect(accountRepository.getAccount(0)).andReturn(account);
        EasyMock.replay(accountRepository);

        Assert.assertTrue("should be available " + cardToTest + ":idToTest=" + idToTest,
                          accountManager.isCreditCardAvailableForAccountUpdate(idToTest, cardToTest));

        EasyMock.verify(accountRepository);
    }

    /**
     * Test isCreditCardAvailableForUpdateCard for card that is in use on different ID
     */
    @Test
    public void testIsCreditCardAvailableForUpdateCardInUseOnDifferentId() {
        final Account account = AccountTestData.createTestAccount0();
        final String cardToTest = AccountTestData.TEST_CREDIT_CARD_1;
        final int idToTest = 0;
        EasyMock.expect(accountRepository.getAccount(0)).andReturn(account);
        EasyMock.expect(accountRepository.hasNoAccountWithCreditCard(cardToTest)).andReturn(false);
        EasyMock.replay(accountRepository);

        Assert.assertFalse("should not be available " + cardToTest + ":idToTest=" + idToTest,
                           accountManager.isCreditCardAvailableForAccountUpdate(idToTest, cardToTest));

        EasyMock.verify(accountRepository);
    }

    /**
     * Test isCreditCardAvailableForUpdateCard for card that is in use and non-existent Id
     */
    @Test
    public void testIsCreditCardAvailableForUpdateCardInUseAndNonExistentId() {
        final String cardToTest = AccountTestData.TEST_CREDIT_CARD_0;
        final int idToTest = 999;
        EasyMock.expect(accountRepository.getAccount(idToTest)).andReturn(null);
        EasyMock.replay(accountRepository);

        try {
            accountManager.isCreditCardAvailableForAccountUpdate(idToTest, cardToTest);
            Assert.fail("expecting EmptyResultDataAccessException for " + cardToTest + ":non-existent id=" + idToTest);
        } catch (EmptyResultDataAccessException ex) {
            EasyMock.verify(accountRepository);
        }
    }
}
