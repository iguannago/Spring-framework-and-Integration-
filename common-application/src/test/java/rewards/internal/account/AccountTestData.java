/**
 *
 */
package rewards.internal.account;

import common.money.MonetaryAmount;
import common.money.Percentage;
import org.junit.Assert;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Test data for account objects
 *
 * @author djnorth
 */
public abstract class AccountTestData {

    public static final String TEST_NUMBER_0 = "123456789";
    public static final String TEST_NAME_0   = "Keith and Keri Donald";
    public static final Date TEST_DOB_0;
    public static final String         TEST_CREDIT_CARD_0                  = "1234123412341234";
    public static final String         TEST_EMAIL_0                        = "keithd@gmail.com";
    public static final boolean        TEST_RECEIVE_NEWSLETTER_0           = true;
    public static final boolean        TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0 = false;
    public static final String         TEST_BENEFICIARY_NAME_0_0           = "Annabelle";
    public static final String         TEST_BENEFICIARY_NAME_0_1           = "Corgan";
    public static final Percentage     TEST_BENEFICIARY_PERCENTAGE_0_0     = Percentage
            .valueOf("50%");
    public static final Percentage     TEST_BENEFICIARY_PERCENTAGE_0_1     = Percentage
            .valueOf("50%");
    public static final MonetaryAmount TEST_BENEFICIARY_SAVINGS_0_0        = new MonetaryAmount(
            250.0);
    public static final MonetaryAmount TEST_BENEFICIARY_SAVINGS_0_1        = new MonetaryAmount(
            125.0);

    public static final String TEST_NUMBER_1 = "123456700";
    public static final String TEST_NAME_1   = "Fred Bloggs";
    public static final Date TEST_DOB_1;
    public static final String         TEST_CREDIT_CARD_1                  = "1234123412341235";
    public static final String         TEST_EMAIL_1                        = "fred_bloggs@hotmail.com";
    public static final boolean        TEST_RECEIVE_NEWSLETTER_1           = false;
    public static final boolean        TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1 = true;
    public static final String         TEST_BENEFICIARY_NAME_1_0           = "Bridgit";
    public static final String         TEST_BENEFICIARY_NAME_1_1           = "Megan";
    public static final String         TEST_BENEFICIARY_NAME_1_2           = "Arnold";
    public static final Percentage     TEST_BENEFICIARY_PERCENTAGE_1_0     = Percentage
            .valueOf("40%");
    public static final Percentage     TEST_BENEFICIARY_PERCENTAGE_1_1     = Percentage
            .valueOf("30%");
    public static final Percentage     TEST_BENEFICIARY_PERCENTAGE_1_2     = Percentage
            .valueOf("30%");
    public static final MonetaryAmount TEST_BENEFICIARY_SAVINGS_1_0        = new MonetaryAmount(
            200.5);
    public static final MonetaryAmount TEST_BENEFICIARY_SAVINGS_1_1        = new MonetaryAmount(
            70.0);
    public static final MonetaryAmount TEST_BENEFICIARY_SAVINGS_1_2        = new MonetaryAmount(
            30.0);

    public static final String TEST_FIRST_DUPLICATE_NAME  = TEST_NAME_0;
    public static final String TEST_SECOND_DUPLICATE_NAME = "Dollie R. Adams";
    public static final String TEST_NAME_UNUSED           = "Dominic North";
    public static final String TEST_NUMBER_UNUSED         = "432100001";
    public static final String TEST_CREDIT_CARD_UNUSED    = "4321123412341001";

    private static final Set<CreditCard> EMPTY_CREDIT_CARDS = new HashSet<CreditCard>();

    static {
        Calendar dobCal0 = Calendar.getInstance();
        dobCal0.set(1981, 3, 11, 0, 0, 0);
        dobCal0.set(Calendar.MILLISECOND, 0);
        TEST_DOB_0 = dobCal0.getTime();

        Calendar dobCal1 = Calendar.getInstance();
        dobCal1.set(1970, 10, 2, 0, 0, 0);
        dobCal1.set(Calendar.MILLISECOND, 0);
        TEST_DOB_1 = dobCal1.getTime();

    }

    /**
     * @return account populated with TEST_xxxx_0, TEST_BENEFICIARY_xxxx_0_*
     *         values
     */
    public static Account createTestAccount0() {
        Account account = new Account(TEST_NUMBER_0,
                                      TEST_NAME_0,
                                      TEST_DOB_0,
                                      TEST_EMAIL_0,
                                      TEST_RECEIVE_NEWSLETTER_0,
                                      TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                                      new CreditCard(TEST_CREDIT_CARD_0));
        account.setEntityId(0);
        account.addBeneficiary(TEST_BENEFICIARY_NAME_0_0, TEST_BENEFICIARY_PERCENTAGE_0_0);
        account.getBeneficiary(TEST_BENEFICIARY_NAME_0_0).credit(TEST_BENEFICIARY_SAVINGS_0_0);
        account.addBeneficiary(TEST_BENEFICIARY_NAME_0_1, TEST_BENEFICIARY_PERCENTAGE_0_1);
        account.getBeneficiary(TEST_BENEFICIARY_NAME_0_1).credit(TEST_BENEFICIARY_SAVINGS_0_1);

        return account;
    }

    /**
     * @return account populated with TEST_xxxx_1, TEST_BENEFICIARY_xxxx_1_*
     *         values (not included in DB script)
     */
    public static Account createTestAccount1() {
        Account account = new Account(TEST_NUMBER_1,
                                      TEST_NAME_1,
                                      TEST_DOB_1,
                                      TEST_EMAIL_1,
                                      TEST_RECEIVE_NEWSLETTER_1,
                                      TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1,
                                      new CreditCard(TEST_CREDIT_CARD_1));

        return account;
    }

    /**
     * @return accountInfo populated with TEST_xxxx_0 *
     *         values and TEST_CREDIT_CARD_0
     */
    public static AccountInfo createTestAccountInfo0() {
        AccountInfo accountInfo = createTestAccountInfo0NoCards();
        accountInfo.addCreditCardInfo(new CreditCardInfo(TEST_CREDIT_CARD_0));
        return accountInfo;
    }


    /**
     * @return accountInfo populated with TEST_xxxx_0 *
     *         values, but no cards
     */
    public static AccountInfo createTestAccountInfo0NoCards() {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setNumber(TEST_NUMBER_0);
        accountInfo.setName(TEST_NAME_0);
        accountInfo.setDateOfBirth(TEST_DOB_0);
        accountInfo.setEmail(TEST_EMAIL_0);
        accountInfo.setReceiveMonthlyEmailUpdate(TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0);
        accountInfo.setReceiveNewsletter(TEST_RECEIVE_NEWSLETTER_0);
        return accountInfo;
    }

    /**
     * @return accountInfo populated with TEST_xxxx_1 *
     *         values (not in DB script)
     */
    public static AccountInfo createTestAccountInfo1() {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setNumber(TEST_NUMBER_1);
        accountInfo.setName(TEST_NAME_1);
        accountInfo.setDateOfBirth(TEST_DOB_1);
        accountInfo.setEmail(TEST_EMAIL_1);
        accountInfo.setReceiveMonthlyEmailUpdate(TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1);
        accountInfo.setReceiveNewsletter(TEST_RECEIVE_NEWSLETTER_1);
        accountInfo.addCreditCardInfo(new CreditCardInfo(TEST_CREDIT_CARD_1));
        return accountInfo;
    }

    /**
     * Create accountBeneficiaryInfo with TEST_xxx_0 values
     */
    public static AccountBeneficiaryInfo createTestAccountBeneficiaryInfo0() {
        return new AccountBeneficiaryInfo(0, TEST_NAME_0, TEST_NUMBER_0);
    }

    /**
     * Verify account with single credit card
     *
     * @param account
     * @param expectedAccountEntityId   (if null, expect null, if < 0, don't check)
     * @param expectedAccountNumber
     * @param expectedAccountName
     * @param expectedCreditCards
     * @param expectedDateOfBirth
     * @param expectedEmail
     * @param expectedReceiveNewsletter
     * @param expectedReceiveMonthlyEmailUpdate
     *
     * @param expectedBeneficiariesSize
     */
    public static void verifyAccount(Account account,
                                     Integer expectedAccountEntityId,
                                     String expectedAccountNumber, String expectedAccountName,
                                     Date expectedDateOfBirth, String expectedEmail,
                                     boolean expectedReceiveNewsletter,
                                     boolean expectedReceiveMonthlyEmailUpdate,
                                     int expectedBeneficiariesSize,
                                     CreditCard... expectedCreditCards) {

        Set<CreditCard> expectedCreditCardSet = new HashSet<CreditCard>();
        if (expectedCreditCards != null && expectedCreditCards.length > 0) {
            for (CreditCard creditCard : expectedCreditCards) {
                expectedCreditCardSet.add(creditCard);
            }
        }

        verifyAccount(account, expectedAccountEntityId, expectedAccountNumber, expectedAccountName,
                      expectedDateOfBirth, expectedEmail, expectedReceiveNewsletter, expectedReceiveMonthlyEmailUpdate,
                      expectedBeneficiariesSize,
                      expectedCreditCardSet);
    }

    /**
     * Verify account
     *
     * @param account
     * @param expectedAccountEntityId   (if null, expect null, if < 0, don't check)
     * @param expectedAccountNumber
     * @param expectedAccountName
     * @param expectedDateOfBirth
     * @param expectedEmail
     * @param expectedReceiveNewsletter
     * @param expectedReceiveMonthlyEmailUpdate
     *
     * @param expectedBeneficiariesSize
     * @param expectedCreditCardSet
     */
    public static void verifyAccount(Account account,
                                     Integer expectedAccountEntityId,
                                     String expectedAccountNumber, String expectedAccountName,
                                     Date expectedDateOfBirth, String expectedEmail,
                                     boolean expectedReceiveNewsletter,
                                     boolean expectedReceiveMonthlyEmailUpdate,
                                     int expectedBeneficiariesSize, Set<CreditCard> expectedCreditCardSet) {

        Assert.assertNotNull("account", account);
        if (expectedAccountEntityId == null || expectedAccountEntityId > 0) {
            Assert.assertEquals("entityId", expectedAccountEntityId,
                                account.getEntityId());
        }

        Assert.assertEquals("accountNumber", expectedAccountNumber,
                            account.getNumber());
        Assert.assertEquals("name", expectedAccountName, account.getName());
        Assert.assertEquals("wrong DoB", expectedDateOfBirth,
                            account.getDateOfBirth());
        Assert.assertEquals("email", expectedEmail, account.getEmail());
        Assert.assertEquals("receiveNewsletter", expectedReceiveNewsletter,
                            account.isReceiveNewsletter());
        Assert.assertEquals("receiveMonthlyEmailUpdate",
                            expectedReceiveMonthlyEmailUpdate,
                            account.isReceiveMonthlyEmailUpdate());
        Assert.assertEquals("beneficiaries.size", expectedBeneficiariesSize,
                            account.getBeneficiaries().size());
        if (expectedCreditCardSet == null) {
            Assert.assertEquals("creditCards", EMPTY_CREDIT_CARDS,
                                account.getCreditCards());
        } else {
            Assert.assertEquals("creditCards", expectedCreditCardSet,
                                account.getCreditCards());
        }

    }

    /**
     * Check account with TEST_xxx_0 values
     *
     * @param account
     * @param expectedBeneficiariesSize
     */
    public static void verifyAccount0(Account account,
                                      int expectedBeneficiariesSize) {
        verifyAccount(account, new Integer(0), TEST_NUMBER_0, TEST_NAME_0, TEST_DOB_0, TEST_EMAIL_0,
                      TEST_RECEIVE_NEWSLETTER_0, TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                      expectedBeneficiariesSize,
                      new CreditCard(TEST_CREDIT_CARD_0));
    }

    /**
     * Verify account
     *
     * @param accountInfo
     * @param expectedAccountEntityId   (if null, expect null, if < 0, don't check)
     * @param expectedAccountNumber
     * @param expectedAccountName
     * @param expectedDateOfBirth
     * @param expectedEmail
     * @param expectedReceiveNewsletter
     * @param expectedReceiveMonthlyEmailUpdate
     *
     * @param expectedCreditCardInfoSet
     */
    public static void verifyAccountInfo(AccountInfo accountInfo,
                                         Integer expectedAccountEntityId,
                                         String expectedAccountNumber,
                                         String expectedAccountName,
                                         Date expectedDateOfBirth,
                                         String expectedEmail,
                                         boolean expectedReceiveNewsletter,
                                         boolean expectedReceiveMonthlyEmailUpdate,
                                         Set<CreditCardInfo> expectedCreditCardInfoSet) {

        if (expectedAccountEntityId == null || expectedAccountEntityId > 0) {
            Assert.assertEquals("entityId", expectedAccountEntityId,
                                accountInfo.getEntityId());
        }

        Assert.assertEquals("accountNumber", expectedAccountNumber,
                            accountInfo.getNumber());
        Assert.assertEquals("name", expectedAccountName, accountInfo.getName());
        Assert.assertEquals("wrong DoB", expectedDateOfBirth,
                            accountInfo.getDateOfBirth());
        Assert.assertEquals("email", expectedEmail, accountInfo.getEmail());
        Assert.assertEquals("receiveNewsletter", expectedReceiveNewsletter,
                            accountInfo.isReceiveNewsletter());
        Assert.assertEquals("receiveMonthlyEmailUpdate",
                            expectedReceiveMonthlyEmailUpdate,
                            accountInfo.isReceiveMonthlyEmailUpdate());
        Assert.assertEquals("creditCardInfo",
                            expectedCreditCardInfoSet,
                            accountInfo.getCreditCardInfo());

    }

    /**
     * Verify account
     *
     * @param accountInfo
     * @param expectedAccountEntityId   (if null, expect null, if < 0, don't check)
     * @param expectedAccountNumber
     * @param expectedAccountName
     * @param expectedDateOfBirth
     * @param expectedEmail
     * @param expectedReceiveNewsletter
     * @param expectedReceiveMonthlyEmailUpdate
     * @param expectedCreditCardInfo
     */
    public static void verifyAccountInfo(AccountInfo accountInfo,
                                         Integer expectedAccountEntityId,
                                         String expectedAccountNumber, String expectedAccountName,
                                         Date expectedDateOfBirth, String expectedEmail,
                                         boolean expectedReceiveNewsletter,
                                         boolean expectedReceiveMonthlyEmailUpdate,
                                         CreditCardInfo... expectedCreditCardInfo) {

        Set<CreditCardInfo> expectedCreditCardInfoSet = new HashSet<CreditCardInfo>();
        for (CreditCardInfo creditCardInfo : expectedCreditCardInfo) {
            expectedCreditCardInfoSet.add(creditCardInfo);
        }

        verifyAccountInfo(accountInfo, expectedAccountEntityId, expectedAccountNumber, expectedAccountName,
                          expectedDateOfBirth, expectedEmail, expectedReceiveNewsletter,
                          expectedReceiveMonthlyEmailUpdate, expectedCreditCardInfoSet);
    }

    /**
     * Check accountINfo with TEST_xxx_0 values
     *
     * @param accountInfo
     */
    public static void verifyAccountInfo0(AccountInfo accountInfo) {
        verifyAccountInfo(accountInfo, new Integer(0), TEST_NUMBER_0, TEST_NAME_0, TEST_DOB_0, TEST_EMAIL_0,
                          TEST_RECEIVE_NEWSLETTER_0, TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                          new CreditCardInfo(TEST_CREDIT_CARD_0));
    }

    /**
     * @param accountBeneficiaryInfo
     * @param expectedName
     * @param expectedAllocationPercentage
     * @param expectedSavings
     */
    public static void verifyBeneficiaryInfo(
            AccountBeneficiaryInfo accountBeneficiaryInfo, String expectedName,
            Percentage expectedAllocationPercentage,
            MonetaryAmount expectedSavings) {

        final String beneficiaryMsg = "beneficiary[" + expectedName + "]";
        BeneficiaryInfo beneficiary = accountBeneficiaryInfo
                .getBeneficiary(expectedName);
        Assert.assertNotNull(beneficiaryMsg, beneficiary);
        Assert.assertEquals(beneficiaryMsg + ":name", expectedName,
                            beneficiary.getName());
        Assert.assertEquals(beneficiaryMsg + ":allocationPercentage",
                            expectedAllocationPercentage,
                            beneficiary.getAllocationPercentage());
        Assert.assertEquals(beneficiaryMsg + ":savings", expectedSavings,
                            beneficiary.getSavings());
    }

    /**
     * @param account
     * @param expectedName
     * @param expectedAllocationPercentage
     * @param expectedSavings
     */
    public static void verifyBeneficiary(Account account, String expectedName,
                                         Percentage expectedAllocationPercentage,
                                         MonetaryAmount expectedSavings) {

        final String beneficiaryMsg = "beneficiary[" + expectedName + "]";
        Beneficiary beneficiary = account.getBeneficiary(expectedName);
        Assert.assertNotNull(beneficiaryMsg, beneficiary);
        Assert.assertEquals(beneficiaryMsg + ":name", expectedName,
                            beneficiary.getName());
        Assert.assertEquals(beneficiaryMsg + ":allocationPercentage",
                            expectedAllocationPercentage,
                            beneficiary.getAllocationPercentage());
        Assert.assertEquals(beneficiaryMsg + ":savings", expectedSavings,
                            beneficiary.getSavings());
    }


    /**
     * Check account info for accountBeneficiaryInfo
     *
     * @param accountBeneficiaryInfo
     * @param expectedAccountEntityId
     * @param expectedAccountName
     * @param expectedAccountNumber
     * @param expectedBeneficiariesSize
     */
    public static void verifyAccountBeneficiaryInfo(
            AccountBeneficiaryInfo accountBeneficiaryInfo,
            Integer expectedAccountEntityId, String expectedAccountName,
            String expectedAccountNumber, int expectedBeneficiariesSize) {
        Assert.assertEquals("accountEntityId", expectedAccountEntityId,
                            accountBeneficiaryInfo.getAccountEntityId());
        Assert.assertEquals("accountName", expectedAccountName,
                            accountBeneficiaryInfo.getAccountName());
        Assert.assertEquals("accountNumber", expectedAccountNumber,
                            accountBeneficiaryInfo.getAccountNumber());
        Assert.assertEquals("beneficiaries count", expectedBeneficiariesSize,
                            accountBeneficiaryInfo.getBeneficiaries().size());
    }

    /**
     * Check accountBeneficiaryInfo with TEST_xxx_0 values
     *
     * @param accountBeneficiaryInfo
     * @param expectedBeneficiariesSize
     */
    public static void verifyAccountBeneficiaryInfo0(
            AccountBeneficiaryInfo accountBeneficiaryInfo,
            int expectedBeneficiariesSize) {
        verifyAccountBeneficiaryInfo(accountBeneficiaryInfo, new Integer(0), TEST_NAME_0,
                                     TEST_NUMBER_0, expectedBeneficiariesSize);
    }
}
