package rewards.internal.account;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for AccountInfo
 *
 * @author Dominic North
 */
public class AccountInfoTests {

    /**
     * Test creation of AccountInfo from Account with null ID, 1 card
     */
    @Test
    public void testCreateAccountInfoFromAccountWithNullID1Card() {
        Account account = AccountTestData.createTestAccount0();
        account.setEntityId(null);
        AccountInfo accountInfo = new AccountInfo(account);

        AccountTestData.verifyAccountInfo(accountInfo, null,
                                          AccountTestData.TEST_NUMBER_0,
                                          AccountTestData.TEST_NAME_0,
                                          AccountTestData.TEST_DOB_0,
                                          AccountTestData.TEST_EMAIL_0,
                                          AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                          AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0));
    }

    /**
     * Test creation of AccountInfo from Account with null ID, 2 cards
     */
    @Test
    public void testCreateAccountInfoFromAccountWithNullID2Cards() {
        Account account = AccountTestData.createTestAccount0();
        account.setEntityId(null);
        account.addCreditCard(new CreditCard(AccountTestData.TEST_CREDIT_CARD_1));
        AccountInfo accountInfo = new AccountInfo(account);

        AccountTestData.verifyAccountInfo(accountInfo, null,
                                          AccountTestData.TEST_NUMBER_0,
                                          AccountTestData.TEST_NAME_0,
                                          AccountTestData.TEST_DOB_0,
                                          AccountTestData.TEST_EMAIL_0,
                                          AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                          AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0),
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));
    }

    /**
     * Test creation of AccountInfo from Account with non-null ID, 1 card
     */
    @Test
    public void testCreateAccountInfoFromAccountWithNonNullID1Card() {
        Account account = AccountTestData.createTestAccount0();
        AccountInfo accountInfo = new AccountInfo(account);

        AccountTestData.verifyAccountInfo(accountInfo, 0,
                                          AccountTestData.TEST_NUMBER_0,
                                          AccountTestData.TEST_NAME_0,
                                          AccountTestData.TEST_DOB_0,
                                          AccountTestData.TEST_EMAIL_0,
                                          AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                          AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0));
    }

    /**
     * Test creation of AccountInfo from Account with non-null ID, 2 cards
     */
    @Test
    public void testCreateAccountInfoFromAccountWithNonNullID2Cards() {
        Account account = AccountTestData.createTestAccount0();
        account.addCreditCard(new CreditCard(AccountTestData.TEST_CREDIT_CARD_1));
        AccountInfo accountInfo = new AccountInfo(account);

        AccountTestData.verifyAccountInfo(accountInfo, 0,
                                          AccountTestData.TEST_NUMBER_0,
                                          AccountTestData.TEST_NAME_0,
                                          AccountTestData.TEST_DOB_0,
                                          AccountTestData.TEST_EMAIL_0,
                                          AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                          AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0),
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));
    }

    /**
     * Test {@link AccountInfo#addCreditCardInfo(CreditCardInfo)} to 0 cards
     */
    @Test
    public void testAddCreditCardInfoTo0Cards() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0NoCards();
        accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0));

        AccountTestData.verifyAccountInfo(accountInfo, null,
                                          AccountTestData.TEST_NUMBER_0,
                                          AccountTestData.TEST_NAME_0,
                                          AccountTestData.TEST_DOB_0,
                                          AccountTestData.TEST_EMAIL_0,
                                          AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                          AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0));
    }

    /**
     * Test {@link AccountInfo#addCreditCardInfo(CreditCardInfo)} to 1 card, not duplicate number
     */
    @Test
    public void testAddCreditCardInfoTo1CardNotDuplicate() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));

        AccountTestData.verifyAccountInfo(accountInfo, null,
                                          AccountTestData.TEST_NUMBER_0,
                                          AccountTestData.TEST_NAME_0,
                                          AccountTestData.TEST_DOB_0,
                                          AccountTestData.TEST_EMAIL_0,
                                          AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                          AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0),
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));
    }

    /**
     * Test {@link AccountInfo#addCreditCardInfo(CreditCardInfo)} to 1 card, duplicate number on same account
     */
    @Test
    public void testAddCreditCardInfoTo1CardDuplicate() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        try {
            accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0));
            Assert.fail("should get DuplicateAccountFieldsException for adding duplicate card " +
                        AccountTestData.TEST_CREDIT_CARD_0 + " to accountInfo=" + accountInfo);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate creditCards, but not accountNumber or accountName:" + ex,
                              ex.hasDuplicateCreditCardNumbers() &&
                              !(ex.hasDuplicateAccountNumber() || ex.hasDuplicateAccountName()));
            Assert.assertEquals("duplicate creditCards", Collections.singleton(AccountTestData.TEST_CREDIT_CARD_0),
                                ex.getDuplicateCreditCardNumbers());
            AccountTestData.verifyAccountInfo(accountInfo, null,
                                              AccountTestData.TEST_NUMBER_0,
                                              AccountTestData.TEST_NAME_0,
                                              AccountTestData.TEST_DOB_0,
                                              AccountTestData.TEST_EMAIL_0,
                                              AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                              AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                                              new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0));
        }
    }

    /**
     * Test {@link rewards.internal.account.AccountInfo#getCreditCardInfoCount()} for 0 cards
     */
    @Test
    public void testGetCreditCardInfoCount0Cards() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0NoCards();

        Assert.assertEquals("should be 0:" + accountInfo, 0, accountInfo.getCreditCardInfoCount());
    }

    /**
     * Test {@link rewards.internal.account.AccountInfo#getCreditCardInfoCount()} for 1 card
     */
    @Test
    public void testGetCreditCardInfoCount1Card() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        Assert.assertEquals("should be 1:" + accountInfo, 1, accountInfo.getCreditCardInfoCount());
    }

    /**
     * Test {@link rewards.internal.account.AccountInfo#getCreditCardInfoCount()} for 2 cards
     */
    @Test
    public void testGetCreditCardInfoCount2Cards() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));

        Assert.assertEquals("should be 2:" + accountInfo, 2, accountInfo.getCreditCardInfoCount());
    }

    /**
     * Test creation of account from account info with null ID, 1 card
     */
    @Test
    public void testCreateAccountFromAccountInfoWithNullID1Card() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        Account account = accountInfo.createAccount();
        AccountTestData.verifyAccount(account, null,
                                      AccountTestData.TEST_NUMBER_0,
                                      AccountTestData.TEST_NAME_0,
                                      AccountTestData.TEST_DOB_0,
                                      AccountTestData.TEST_EMAIL_0,
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0, 0,
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));
    }

    /**
     * Test creation of account from account info with null ID, 2 cards
     */
    @Test
    public void testCreateAccountFromAccountInfoWithNullID2Cards() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));
        Account account = accountInfo.createAccount();

        AccountTestData.verifyAccount(account, null,
                                      AccountTestData.TEST_NUMBER_0,
                                      AccountTestData.TEST_NAME_0,
                                      AccountTestData.TEST_DOB_0,
                                      AccountTestData.TEST_EMAIL_0,
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0, 0,
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_0),
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_1));
    }

    /**
     * Test creation of account from account info with non-null ID, 1 card (still gives null ID in account)
     */
    @Test
    public void testCreateAccountFromAccountInfoWithNonNullID1Card() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        accountInfo.setEntityId(0);

        Account account = accountInfo.createAccount();
        AccountTestData.verifyAccount(account, null,
                                      AccountTestData.TEST_NUMBER_0,
                                      AccountTestData.TEST_NAME_0,
                                      AccountTestData.TEST_DOB_0,
                                      AccountTestData.TEST_EMAIL_0,
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0, 0,
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));
    }

    /**
     * Test creation of account from account info with null ID, 2 cards
     */
    @Test
    public void testCreateAccountFromAccountInfoWithNonNullID2Cards() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));

        Account account = accountInfo.createAccount();

        AccountTestData.verifyAccount(account, null,
                                      AccountTestData.TEST_NUMBER_0,
                                      AccountTestData.TEST_NAME_0,
                                      AccountTestData.TEST_DOB_0,
                                      AccountTestData.TEST_EMAIL_0,
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0, 0,
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_0),
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_1));
    }


    /**
     * Test updateAccountInfo of account with null ID, different account number (leaves account number unchanged)
     */
    @Test
    public void testUpdateFromAccountInfoWithNullIDNumberDifferent1CardSame() {
        Account account = AccountTestData.createTestAccount0();
        AccountInfo accountInfo = new AccountInfo(account);

        accountInfo.setEntityId(null);
        accountInfo.setNumber(AccountTestData.TEST_NUMBER_1);
        accountInfo.setName(AccountTestData.TEST_NAME_1);
        accountInfo.setDateOfBirth(AccountTestData.TEST_DOB_1);
        accountInfo.setEmail(AccountTestData.TEST_EMAIL_1);
        accountInfo.setReceiveMonthlyEmailUpdate(AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1);
        accountInfo.setReceiveNewsletter(AccountTestData.TEST_RECEIVE_NEWSLETTER_1);

        accountInfo.updateAccount(account);

        AccountTestData.verifyAccount(account, 0,
                                      AccountTestData.TEST_NUMBER_0,
                                      AccountTestData.TEST_NAME_1,
                                      AccountTestData.TEST_DOB_1,
                                      AccountTestData.TEST_EMAIL_1,
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_1,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1, 2,
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));

        AccountTestData.verifyAccountInfo(accountInfo, null,
                                          AccountTestData.TEST_NUMBER_1,
                                          AccountTestData.TEST_NAME_1,
                                          AccountTestData.TEST_DOB_1,
                                          AccountTestData.TEST_EMAIL_1,
                                          AccountTestData.TEST_RECEIVE_NEWSLETTER_1,
                                          AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1,
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0));
    }

    /**
     * Test updateAccountInfo of account with non-null ID, different account number and 2 new different cards (leaves
     * account number unchanged)
     */
    @Test
    public void testUpdateFromAccountInfoWithNonNullIDNumberDifferent1CardDifferent() {
        Account account = AccountTestData.createTestAccount0();
        AccountInfo accountInfo = new AccountInfo(account);

        accountInfo.setEntityId(1);
        accountInfo.setNumber(AccountTestData.TEST_NUMBER_1);
        accountInfo.setName(AccountTestData.TEST_NAME_1);
        accountInfo.setDateOfBirth(AccountTestData.TEST_DOB_1);
        accountInfo.setEmail(AccountTestData.TEST_EMAIL_1);
        accountInfo.setReceiveMonthlyEmailUpdate(AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1);
        accountInfo.setReceiveNewsletter(AccountTestData.TEST_RECEIVE_NEWSLETTER_1);
        accountInfo.removeCreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0);
        accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));
        accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_UNUSED));

        accountInfo.updateAccount(account);

        AccountTestData.verifyAccount(account, 0,
                                      AccountTestData.TEST_NUMBER_0,
                                      AccountTestData.TEST_NAME_1,
                                      AccountTestData.TEST_DOB_1,
                                      AccountTestData.TEST_EMAIL_1,
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_1,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1, 2,
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_1),
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_UNUSED));

        AccountTestData.verifyAccountInfo(accountInfo, 1,
                                          AccountTestData.TEST_NUMBER_1,
                                          AccountTestData.TEST_NAME_1,
                                          AccountTestData.TEST_DOB_1,
                                          AccountTestData.TEST_EMAIL_1,
                                          AccountTestData.TEST_RECEIVE_NEWSLETTER_1,
                                          AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1,
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1),
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_UNUSED));
    }

    /**
     * Test updateAccountInfo of account with null ID, one additional card
     */
    @Test
    public void testUpdateFromAccountInfoWithNullIDNumberDifferentTwoCreditCards() {
        Account account = AccountTestData.createTestAccount0();
        AccountInfo accountInfo = new AccountInfo(account);

        accountInfo.setEntityId(null);
        accountInfo.setNumber(AccountTestData.TEST_NUMBER_1);
        accountInfo.setName(AccountTestData.TEST_NAME_1);
        accountInfo.setDateOfBirth(AccountTestData.TEST_DOB_1);
        accountInfo.setEmail(AccountTestData.TEST_EMAIL_1);
        accountInfo.setReceiveMonthlyEmailUpdate(AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1);
        accountInfo.setReceiveNewsletter(AccountTestData.TEST_RECEIVE_NEWSLETTER_1);

        accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));

        accountInfo.updateAccount(account);

        AccountTestData.verifyAccount(account, 0,
                                      AccountTestData.TEST_NUMBER_0,
                                      AccountTestData.TEST_NAME_1,
                                      AccountTestData.TEST_DOB_1,
                                      AccountTestData.TEST_EMAIL_1,
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_1,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1, 2,
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_0),
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_1));

        AccountTestData.verifyAccountInfo(accountInfo, null,
                                          AccountTestData.TEST_NUMBER_1,
                                          AccountTestData.TEST_NAME_1,
                                          AccountTestData.TEST_DOB_1,
                                          AccountTestData.TEST_EMAIL_1,
                                          AccountTestData.TEST_RECEIVE_NEWSLETTER_1,
                                          AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1,
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0),
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));
    }

    /**
     * Test updateAccountInfo of account with non-null ID, different account number,
     * one new credit card giving two credit cards
     */
    @Test
    public void testUpdateFromAccountInfoWithNonNullIDNumberDifferentTwoCreditCards() {
        Account account = AccountTestData.createTestAccount0();
        AccountInfo accountInfo = new AccountInfo(account);

        accountInfo.setEntityId(1);
        accountInfo.setNumber(AccountTestData.TEST_NUMBER_1);
        accountInfo.setName(AccountTestData.TEST_NAME_1);
        accountInfo.setDateOfBirth(AccountTestData.TEST_DOB_1);
        accountInfo.setEmail(AccountTestData.TEST_EMAIL_1);
        accountInfo.setReceiveNewsletter(AccountTestData.TEST_RECEIVE_NEWSLETTER_1);
        accountInfo.setReceiveMonthlyEmailUpdate(AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1);
        accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));

        accountInfo.updateAccount(account);

        AccountTestData.verifyAccount(account, 0,
                                      AccountTestData.TEST_NUMBER_0,
                                      AccountTestData.TEST_NAME_1,
                                      AccountTestData.TEST_DOB_1,
                                      AccountTestData.TEST_EMAIL_1,
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_1,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1, 2,
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_0),
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_1));

        AccountTestData.verifyAccountInfo(accountInfo, 1,
                                          AccountTestData.TEST_NUMBER_1,
                                          AccountTestData.TEST_NAME_1,
                                          AccountTestData.TEST_DOB_1,
                                          AccountTestData.TEST_EMAIL_1,
                                          AccountTestData.TEST_RECEIVE_NEWSLETTER_1,
                                          AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1,
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0),
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));
    }

    /**
     * Test updateAccountInfo of account with non-null ID, zero credit cards
     */
    @Test
    public void testUpdateFromAccountInfo0CreditCards() {
        Account account = AccountTestData.createTestAccount0();
        AccountInfo accountInfo = new AccountInfo(account);

        accountInfo.setEntityId(null);
        accountInfo.setNumber(AccountTestData.TEST_NUMBER_1);
        accountInfo.setName(AccountTestData.TEST_NAME_1);
        accountInfo.setDateOfBirth(AccountTestData.TEST_DOB_1);
        accountInfo.setEmail(AccountTestData.TEST_EMAIL_1);
        accountInfo.setReceiveNewsletter(AccountTestData.TEST_RECEIVE_NEWSLETTER_1);
        accountInfo.setReceiveMonthlyEmailUpdate(AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1);
        accountInfo.removeCreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0);

        try {
            accountInfo.updateAccount(account);
            Assert.fail("expected AccountZeroCreditCardsException for accountInfo with 0 cards:" + accountInfo);
        } catch (AccountZeroCreditCardsException azce) {
            AccountTestData.verifyAccount(account, 0,
                                          AccountTestData.TEST_NUMBER_0,
                                          AccountTestData.TEST_NAME_0,
                                          AccountTestData.TEST_DOB_0,
                                          AccountTestData.TEST_EMAIL_0,
                                          AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                          AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0, 2,
                                          new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));

            AccountTestData.verifyAccountInfo(accountInfo, null,
                                              AccountTestData.TEST_NUMBER_1,
                                              AccountTestData.TEST_NAME_1,
                                              AccountTestData.TEST_DOB_1,
                                              AccountTestData.TEST_EMAIL_1,
                                              AccountTestData.TEST_RECEIVE_NEWSLETTER_1,
                                              AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1);
        }
    }

    /**
     * Test all setters not relating to cards
     */
    @Test
    public void testNonCreditCardSetters() {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setNumber(AccountTestData.TEST_NUMBER_0);
        accountInfo.setName(AccountTestData.TEST_NAME_0);
        accountInfo.setDateOfBirth(AccountTestData.TEST_DOB_0);
        accountInfo.setEmail(AccountTestData.TEST_EMAIL_0);
        accountInfo.setReceiveMonthlyEmailUpdate(AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0);
        accountInfo.setReceiveNewsletter(AccountTestData.TEST_RECEIVE_NEWSLETTER_0);

        AccountTestData.verifyAccountInfo(accountInfo, null,
                                          AccountTestData.TEST_NUMBER_0,
                                          AccountTestData.TEST_NAME_0,
                                          AccountTestData.TEST_DOB_0,
                                          AccountTestData.TEST_EMAIL_0,
                                          AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                          AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0);
    }

    /**
     * Test getCreditCardInfo with 0 cards
     */
    @Test
    public void testGetCreditCardInfo0Cards() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0NoCards();

        Set<CreditCardInfo> expectedCreditCardInfo = new HashSet<CreditCardInfo>();
        Set<CreditCardInfo> creditCards = accountInfo.getCreditCardInfo();
        Assert.assertEquals("creditCards got", expectedCreditCardInfo, creditCards);

        AccountTestData.verifyAccountInfo(accountInfo, 0,
                                          AccountTestData.TEST_NUMBER_0,
                                          AccountTestData.TEST_NAME_0,
                                          AccountTestData.TEST_DOB_0,
                                          AccountTestData.TEST_EMAIL_0,
                                          AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                          AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0);

    }

    /**
     * Test getCreditCardInfo with 1 card
     */
    @Test
    public void testGetCreditCardInfo1Card() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        Set<CreditCardInfo> expectedCreditCardInfo = new HashSet<CreditCardInfo>();
        expectedCreditCardInfo.add(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0));
        Set<CreditCardInfo> creditCards = accountInfo.getCreditCardInfo();
        Assert.assertEquals("creditCards got", expectedCreditCardInfo, creditCards);

        AccountTestData.verifyAccountInfo(accountInfo, 0,
                                          AccountTestData.TEST_NUMBER_0,
                                          AccountTestData.TEST_NAME_0,
                                          AccountTestData.TEST_DOB_0,
                                          AccountTestData.TEST_EMAIL_0,
                                          AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                          AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0));

    }

    /**
     * Test getCreditCardInfo with 2 cards
     */
    @Test
    public void testGetCreditCardInfo2Cards() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));

        Set<CreditCardInfo> expectedCreditCardInfo = new HashSet<CreditCardInfo>();
        expectedCreditCardInfo.add(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0));
        expectedCreditCardInfo.add(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));

        Set<CreditCardInfo> creditCards = accountInfo.getCreditCardInfo();
        Assert.assertEquals("creditCards got", expectedCreditCardInfo, creditCards);

        AccountTestData.verifyAccountInfo(accountInfo, 0,
                                          AccountTestData.TEST_NUMBER_0,
                                          AccountTestData.TEST_NAME_0,
                                          AccountTestData.TEST_DOB_0,
                                          AccountTestData.TEST_EMAIL_0,
                                          AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                          AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0),
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));

    }

    /**
     * Test setCreditCardInfo with 0 cards set to 0 cards
     */
    @Test
    public void testSetCreditCardInfo0to0Cards() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0NoCards();

        Set<CreditCardInfo> expectedCreditCardInfo = new HashSet<CreditCardInfo>();
        Set<CreditCardInfo> creditCards = new HashSet<CreditCardInfo>(expectedCreditCardInfo);
        accountInfo.setCreditCardInfo(creditCards);
        Assert.assertEquals("creditCards set", expectedCreditCardInfo, accountInfo.getCreditCardInfo());
    }

    /**
     * Test setCreditCardInfo with 1 cards set to 0 cards
     */
    @Test
    public void testSetCreditCardInfo1to0Cards() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        Set<CreditCardInfo> expectedCreditCardInfo = new HashSet<CreditCardInfo>();
        Set<CreditCardInfo> creditCards = new HashSet<CreditCardInfo>(expectedCreditCardInfo);
        accountInfo.setCreditCardInfo(creditCards);
        Assert.assertEquals("creditCards set", expectedCreditCardInfo, accountInfo.getCreditCardInfo());
    }

    /**
     * Test setCreditCardInfo with 0 cards set to 1 card
     */
    @Test
    public void testSetCreditCardInfo0to1Card() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0NoCards();

        Set<CreditCardInfo> expectedCreditCardInfo = new HashSet<CreditCardInfo>();
        expectedCreditCardInfo.add(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0));
        Set<CreditCardInfo> creditCards = new HashSet<CreditCardInfo>(expectedCreditCardInfo);
        accountInfo.setCreditCardInfo(creditCards);
        Assert.assertEquals("creditCards set", expectedCreditCardInfo, accountInfo.getCreditCardInfo());
    }

    /**
     * Test setCreditCardInfo with 1 cards set to 1 card (the same)
     */
    @Test
    public void testSetCreditCardInfo1to1CardEqual() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        Set<CreditCardInfo> expectedCreditCardInfo = new HashSet<CreditCardInfo>();
        expectedCreditCardInfo.add(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0));
        Set<CreditCardInfo> creditCards = new HashSet<CreditCardInfo>(expectedCreditCardInfo);
        accountInfo.setCreditCardInfo(creditCards);
        Assert.assertEquals("creditCards set", expectedCreditCardInfo, accountInfo.getCreditCardInfo());
    }

    /**
     * Test setCreditCardInfo with 1 cards set to 1 card (different)
     */
    @Test
    public void testSetCreditCardInfo1to1CardsNotEqual() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        Set<CreditCardInfo> expectedCreditCardInfo = new HashSet<CreditCardInfo>();
        expectedCreditCardInfo.add(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));
        Set<CreditCardInfo> creditCards = new HashSet<CreditCardInfo>(expectedCreditCardInfo);
        accountInfo.setCreditCardInfo(creditCards);
        Assert.assertEquals("creditCards set", expectedCreditCardInfo, accountInfo.getCreditCardInfo());
    }

    /**
     * Test setCreditCardInfo with 1 cards set to 2 cards (1 equal, 1 different)
     */
    @Test
    public void testSetCreditCardInfo1to2Cards1NotEqual() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        Set<CreditCardInfo> expectedCreditCardInfo = new HashSet<CreditCardInfo>();
        expectedCreditCardInfo.add(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0));
        expectedCreditCardInfo.add(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));
        Set<CreditCardInfo> creditCards = new HashSet<CreditCardInfo>(expectedCreditCardInfo);
        accountInfo.setCreditCardInfo(creditCards);
        Assert.assertEquals("creditCards set", expectedCreditCardInfo, accountInfo.getCreditCardInfo());
    }

    /**
     * Test setCreditCardInfo with 1 cards set to 2 cards (2 different)
     */
    @Test
    public void testSetCreditCardInfo1to2Cards2NotEqual() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        Set<CreditCardInfo> expectedCreditCardInfo = new HashSet<CreditCardInfo>();
        expectedCreditCardInfo.add(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_UNUSED));
        expectedCreditCardInfo.add(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));
        Set<CreditCardInfo> creditCards = new HashSet<CreditCardInfo>(expectedCreditCardInfo);
        accountInfo.setCreditCardInfo(creditCards);
        Assert.assertEquals("creditCards set", expectedCreditCardInfo, accountInfo.getCreditCardInfo());
    }

    /**
     * Test getCreditCard from 0 cards, card doesn't exist
     */
    @Test
    public void testGetCreditCard0CardsCardNonExistent() {
        final String creditCardNumberToTest = AccountTestData.TEST_CREDIT_CARD_0;
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0NoCards();

        CreditCardInfo creditCard = null;

        try {
            creditCard = accountInfo.getCreditCard(creditCardNumberToTest);
            Assert.fail("creditCard shouldn't be found for creditCardNumber=" + creditCardNumberToTest + ":card=" +
                        creditCard);
        } catch (EmptyResultDataAccessException ex) {
            final String exmsg = "creditCardNumber='" + creditCardNumberToTest + "'";
            Assert.assertTrue("exception msg '" + ex.getMessage() + "' should contain info:" + exmsg,
                              ex.getMessage().contains(exmsg));
        }
    }

    /**
     * Test getCreditCard from 1 cards, card exists
     */
    @Test
    public void testGetCreditCard1CardsCardExists() {
        final String creditCardNumberToTest = AccountTestData.TEST_CREDIT_CARD_0;
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        CreditCardInfo expectedCreditCard = new CreditCardInfo(creditCardNumberToTest);

        CreditCardInfo creditCard = accountInfo.getCreditCard(creditCardNumberToTest);
        Assert.assertEquals("creditCard got", expectedCreditCard, creditCard);

        AccountTestData.verifyAccountInfo(accountInfo, 0,
                                          AccountTestData.TEST_NUMBER_0,
                                          AccountTestData.TEST_NAME_0,
                                          AccountTestData.TEST_DOB_0,
                                          AccountTestData.TEST_EMAIL_0,
                                          AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                          AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                                          new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0));
    }

    /**
     * Test getCreditCard from 1 cards, card doesn't exist
     */
    @Test
    public void testGetCreditCard1CardsCardNonExistent() {
        final String creditCardNumberToTest = AccountTestData.TEST_CREDIT_CARD_1;
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        CreditCardInfo creditCard = null;

        try {
            creditCard = accountInfo.getCreditCard(creditCardNumberToTest);
            Assert.fail("creditCard shouldn't be found for creditCardNumber=" + creditCardNumberToTest + ":card=" +
                        creditCard);
        } catch (EmptyResultDataAccessException ex) {
            final String exmsg = "creditCardNumber='" + creditCardNumberToTest + "'";
            Assert.assertTrue("exception msg '" + ex.getMessage() + "' should contain info:" + exmsg,
                              ex.getMessage().contains(exmsg));

            AccountTestData.verifyAccountInfo(accountInfo, 0,
                                              AccountTestData.TEST_NUMBER_0,
                                              AccountTestData.TEST_NAME_0,
                                              AccountTestData.TEST_DOB_0,
                                              AccountTestData.TEST_EMAIL_0,
                                              AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                              AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                                              new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0));
        }
    }

    /**
     * Test getCreditCard from 2 cards, card exists
     */
    @Test
    public void testGetCreditCard2CardsCardExists() {
        final String creditCardNumberToTest = AccountTestData.TEST_CREDIT_CARD_0;
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));

        CreditCardInfo expectedCreditCard = new CreditCardInfo(creditCardNumberToTest);

        CreditCardInfo creditCard = accountInfo.getCreditCard(creditCardNumberToTest);
        Assert.assertEquals("creditCard got", expectedCreditCard, creditCard);
    }

    /**
     * Test getCreditCard from 2 cards, card doesn't exist
     */
    @Test
    public void testGetCreditCard2CardsCardNonExistent() {
        final String creditCardNumberToTest = AccountTestData.TEST_CREDIT_CARD_UNUSED;
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));

        CreditCardInfo creditCard = null;

        try {
            creditCard = accountInfo.getCreditCard(creditCardNumberToTest);
            Assert.fail("creditCard shouldn't be found for creditCardNumber=" + creditCardNumberToTest + ":card=" +
                        creditCard);
        } catch (EmptyResultDataAccessException ex) {
            final String exmsg = "creditCardNumber='" + creditCardNumberToTest + "'";
            Assert.assertTrue("exception msg '" + ex.getMessage() + "' should contain info:" + exmsg,
                              ex.getMessage().contains(exmsg));
        }
    }

    /**
     * Test getCreditCardNumbers with 0 cards
     */
    @Test
    public void testGetCreditCardNumbers0Cards() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0NoCards();

        Set<String> expectedCreditCardNumbers = new HashSet<String>();

        Set<String> creditCardNumbers = accountInfo.getCreditCardNumbers();
        Assert.assertEquals("creditCardNumbers got", expectedCreditCardNumbers, creditCardNumbers);
    }

    /**
     * Test getCreditCardNumbers with 1 card
     */
    @Test
    public void testGetCreditCardNumbers1Card() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        Set<String> expectedCreditCardNumbers = new HashSet<String>();
        expectedCreditCardNumbers.add(AccountTestData.TEST_CREDIT_CARD_0);

        Set<String> creditCardNumbers = accountInfo.getCreditCardNumbers();
        Assert.assertEquals("creditCardNumbers got", expectedCreditCardNumbers, creditCardNumbers);
    }

    /**
     * Test getCreditCardNumbers with 2 cards
     */
    @Test
    public void testGetCreditCardNumbers2Cards() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));

        Set<String> expectedCreditCardNumbers = new HashSet<String>();
        expectedCreditCardNumbers.add(AccountTestData.TEST_CREDIT_CARD_0);
        expectedCreditCardNumbers.add(AccountTestData.TEST_CREDIT_CARD_1);

        Set<String> creditCardNumbers = accountInfo.getCreditCardNumbers();
        Assert.assertEquals("creditCardNumbers got", expectedCreditCardNumbers, creditCardNumbers);
    }

    /**
     * Test setCreditCardNumbers with 0 cards set to 0 cards
     */
    @Test
    public void testSetCreditCardNumbers0to0Cards() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0NoCards();

        Set<String> expectedCreditCardNumbers = new HashSet<String>();

        Set<String> creditCardNumbers = new HashSet<String>(expectedCreditCardNumbers);

        accountInfo.setCreditCardNumbers(creditCardNumbers);
        Assert.assertEquals("creditCardNumbers set", expectedCreditCardNumbers, accountInfo.getCreditCardNumbers());
    }

    /**
     * Test setCreditCardNumbers with 0 card set to 1 card
     */
    @Test
    public void testSetCreditCardNumbers0to1Card() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0NoCards();

        Set<String> expectedCreditCardNumbers = new HashSet<String>();
        expectedCreditCardNumbers.add(AccountTestData.TEST_CREDIT_CARD_0);

        Set<String> creditCardNumbers = new HashSet<String>(expectedCreditCardNumbers);

        accountInfo.setCreditCardNumbers(creditCardNumbers);
        Assert.assertEquals("creditCardNumbers set", expectedCreditCardNumbers, accountInfo.getCreditCardNumbers());
    }

    /**
     * Test setCreditCardNumbers with 1 card set to 0 cards
     */
    @Test
    public void testSetCreditCardNumbers1to0Cards() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        Set<String> expectedCreditCardNumbers = new HashSet<String>();

        Set<String> creditCardNumbers = new HashSet<String>(expectedCreditCardNumbers);

        accountInfo.setCreditCardNumbers(creditCardNumbers);
        Assert.assertEquals("creditCardNumbers set", expectedCreditCardNumbers, accountInfo.getCreditCardNumbers());
    }

    /**
     * Test setCreditCardNumbers with 1 card set to 1 card (same)
     */
    @Test
    public void testSetCreditCardNumbers1to1Card1Equal() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        Set<String> expectedCreditCardNumbers = new HashSet<String>();
        expectedCreditCardNumbers.add(AccountTestData.TEST_CREDIT_CARD_0);

        Set<String> creditCardNumbers = new HashSet<String>(expectedCreditCardNumbers);

        accountInfo.setCreditCardNumbers(creditCardNumbers);
        Assert.assertEquals("creditCardNumbers set", expectedCreditCardNumbers, accountInfo.getCreditCardNumbers());
    }

    /**
     * Test setCreditCardNumbers with 1 card set to 1 card (different)
     */
    @Test
    public void testSetCreditCardNumbers1to1Cards1NotEqual() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        Set<String> expectedCreditCardNumbers = new HashSet<String>();
        expectedCreditCardNumbers.add(AccountTestData.TEST_CREDIT_CARD_1);

        Set<String> creditCardNumbers = new HashSet<String>(expectedCreditCardNumbers);

        accountInfo.setCreditCardNumbers(creditCardNumbers);
        Assert.assertEquals("creditCardNumbers set", expectedCreditCardNumbers, accountInfo.getCreditCardNumbers());
    }

    /**
     * Test setCreditCardNumbers with 1 cards set to 2 cards (1 same, 1 different)
     */
    @Test
    public void testSetCreditCardNumbers1to2Cards1NotEqual() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        Set<String> expectedCreditCardNumbers = new HashSet<String>();
        expectedCreditCardNumbers.add(AccountTestData.TEST_CREDIT_CARD_0);
        expectedCreditCardNumbers.add(AccountTestData.TEST_CREDIT_CARD_1);

        Set<String> creditCardNumbers = new HashSet<String>(expectedCreditCardNumbers);

        accountInfo.setCreditCardNumbers(creditCardNumbers);
        Assert.assertEquals("creditCardNumbers set", expectedCreditCardNumbers, accountInfo.getCreditCardNumbers());
    }

    /**
     * Test setCreditCardNumbers with 1 cards set to 2 cards (2 different)
     */
    @Test
    public void testSetCreditCardNumbers1to2Cards2NotEqual() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        Set<String> expectedCreditCardNumbers = new HashSet<String>();
        expectedCreditCardNumbers.add(AccountTestData.TEST_CREDIT_CARD_UNUSED);
        expectedCreditCardNumbers.add(AccountTestData.TEST_CREDIT_CARD_1);

        Set<String> creditCardNumbers = new HashSet<String>(expectedCreditCardNumbers);

        accountInfo.setCreditCardNumbers(creditCardNumbers);
        Assert.assertEquals("creditCardNumbers set", expectedCreditCardNumbers, accountInfo.getCreditCardNumbers());
    }

    /**
     * Test getCreditCardNumber with 0 cards
     */
    @Test
    public void testGetCreditCardNumber0Cards() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0NoCards();

        String creditCardNumber = accountInfo.getCreditCardNumber();
        Assert.assertNull("creditCardNumbers got", creditCardNumber);
    }

    /**
     * Test getCreditCardNumber with 1 card
     */
    @Test
    public void testGetCreditCardNumber1Card() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        String expectedCreditCardNumber = AccountTestData.TEST_CREDIT_CARD_0;

        String creditCardNumber = accountInfo.getCreditCardNumber();
        Assert.assertEquals("creditCardNumbers got", expectedCreditCardNumber, creditCardNumber);
    }

    /**
     * Test getCreditCardNumber with 2 cards
     */
    @Test
    public void testGetCreditCardNumber2Cards() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));

        Set<String> expectedCreditCardNumbers = new HashSet<String>();
        expectedCreditCardNumbers.add(AccountTestData.TEST_CREDIT_CARD_0);
        expectedCreditCardNumbers.add(AccountTestData.TEST_CREDIT_CARD_1);
        String expectedCreditCardNumber =
                expectedCreditCardNumbers.toArray(new String[expectedCreditCardNumbers.size()])[0];

        String creditCardNumber = accountInfo.getCreditCardNumber();
        Assert.assertEquals("creditCardNumbers got", expectedCreditCardNumber, creditCardNumber);
    }

    /**
     * Test setCreditCardNumber with null card
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetCreditCardNumberNull() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0NoCards();

        accountInfo.setCreditCardNumber(null);
    }

    /**
     * Test setCreditCardNumber with 1 card set to 1 card (same)
     */
    @Test
    public void testSetCreditCardNumber1to1CardSame() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        Set<String> expectedCreditCardNumbers = new HashSet<String>();
        expectedCreditCardNumbers.add(AccountTestData.TEST_CREDIT_CARD_0);

        Set<String> creditCardNumbers = new HashSet<String>(expectedCreditCardNumbers);

        accountInfo.setCreditCardNumber(AccountTestData.TEST_CREDIT_CARD_0);
        Assert.assertEquals("creditCardNumbers set", expectedCreditCardNumbers, accountInfo.getCreditCardNumbers());
    }

    /**
     * Test setCreditCardNumber with 1 card set to 1 card (different)
     */
    @Test
    public void testSetCreditCardNumber1to1CardDifferent() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();

        Set<String> expectedCreditCardNumbers = new HashSet<String>();
        expectedCreditCardNumbers.add(AccountTestData.TEST_CREDIT_CARD_1);

        Set<String> creditCardNumbers = new HashSet<String>(expectedCreditCardNumbers);

        accountInfo.setCreditCardNumber(AccountTestData.TEST_CREDIT_CARD_1);
        Assert.assertEquals("creditCardNumbers set", expectedCreditCardNumbers, accountInfo.getCreditCardNumbers());
    }

    /**
     * Test setCreditCardNumber with 2 cards set to 1 card (not included, no change)
     */
    @Test
    public void testSetCreditCardNumber2to1CardIncluded() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));

        Set<String> expectedCreditCardNumbers = new HashSet<String>();
        expectedCreditCardNumbers.add(AccountTestData.TEST_CREDIT_CARD_0);
        expectedCreditCardNumbers.add(AccountTestData.TEST_CREDIT_CARD_1);

        accountInfo.setCreditCardNumber(AccountTestData.TEST_CREDIT_CARD_1);
        Assert.assertEquals("creditCardNumbers set", expectedCreditCardNumbers, accountInfo.getCreditCardNumbers());
    }

    /**
     * Test setCreditCardNumber with 2 cards set to 1 card (not included, exception)
     */
    @Test(expected = IllegalStateException.class)
    public void testSetCreditCardNumber2to1CardNotIncluded() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));

        accountInfo.setCreditCardNumber(AccountTestData.TEST_CREDIT_CARD_UNUSED);
    }

    /**
     * Test equals (same object)
     */
    @Test
    public void testEqualsWhenSame() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        Assert.assertEquals("same object should be equal", accountInfo, accountInfo);
    }

    /**
     * Test equals (same values including ID)
     */
    @Test
    public void testEqualsWhenEqualIncludingId() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        AccountInfo otherAccountInfo = AccountTestData.createTestAccountInfo0();
        Assert.assertTrue("same values should be equal", accountInfo.equals(otherAccountInfo));
        Assert.assertTrue("same values should be equal (reversed)", otherAccountInfo.equals(accountInfo));
    }

    /**
     * Test equals (same values excluding ID)
     */
    @Test
    public void testEqualsWhenEqualExcludingId() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        AccountInfo otherAccountInfo = AccountTestData.createTestAccountInfo0();
        otherAccountInfo.setEntityId(0);
        Assert.assertTrue("same values should be equal", accountInfo.equals(otherAccountInfo));
        Assert.assertTrue("same values should be equal (reversed)", otherAccountInfo.equals(accountInfo));
    }

    /**
     * Test (not) equals (null)
     */
    @Test
    public void testNotEqualsWhenOtherNull() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        Assert.assertFalse("null should not be equal", accountInfo.equals(null));
    }

    /**
     * Test (not) equals (different number)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentNumber() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        AccountInfo otherAccountInfo = AccountTestData.createTestAccountInfo0();
        otherAccountInfo.setNumber(AccountTestData.TEST_NUMBER_1);
        Assert.assertFalse("different number should not be equal", accountInfo.equals(otherAccountInfo));
    }

    /**
     * Test (not) equals (different name)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentName() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        AccountInfo otherAccountInfo = AccountTestData.createTestAccountInfo0();
        otherAccountInfo.setName(AccountTestData.TEST_NAME_1);
        Assert.assertFalse("different name should not be equal", accountInfo.equals(otherAccountInfo));
    }

    /**
     * Test (not) equals (different DoB)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentDateOfBirth() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        AccountInfo otherAccountInfo = AccountTestData.createTestAccountInfo0();
        otherAccountInfo.setDateOfBirth(AccountTestData.TEST_DOB_1);
        Assert.assertFalse("different DoB should not be equal", accountInfo.equals(otherAccountInfo));
    }

    /**
     * Test (not) equals (different email)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentEMail() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        AccountInfo otherAccountInfo = AccountTestData.createTestAccountInfo0();
        otherAccountInfo.setEmail(AccountTestData.TEST_EMAIL_1);
        Assert.assertFalse("different email should not be equal", accountInfo.equals(otherAccountInfo));
    }

    /**
     * Test (not) equals (different receive newsletter)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentReceiveNewsletter() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        AccountInfo otherAccountInfo = AccountTestData.createTestAccountInfo0();
        otherAccountInfo.setReceiveNewsletter(!AccountTestData.TEST_RECEIVE_NEWSLETTER_0);
        Assert.assertFalse("different email should not be equal", accountInfo.equals(otherAccountInfo));
    }

    /**
     * Test (not) equals (different receive email)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentReceiveEMail() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        AccountInfo otherAccountInfo = AccountTestData.createTestAccountInfo0();
        otherAccountInfo.setReceiveMonthlyEmailUpdate(!AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0);
        Assert.assertFalse("different receive email should not be equal", accountInfo.equals(otherAccountInfo));
    }

    /**
     * Test (not) equals (different credit cards)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentCreditCardsSameSize() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        AccountInfo otherAccountInfo = AccountTestData.createTestAccountInfo0();
        otherAccountInfo.removeCreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0);
        otherAccountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));
        Assert.assertFalse("different credit cards (same size) should not be equal",
                           accountInfo.equals(otherAccountInfo));
    }

    /**
     * Test (not) equals (different credit cards)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentCreditCardsDifferentSize() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo0();
        AccountInfo otherAccountInfo = AccountTestData.createTestAccountInfo0();
        otherAccountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_1));
        Assert.assertFalse("different credit cards (different size) should not be equal",
                           accountInfo.equals(otherAccountInfo));
    }
}
