package rewards.internal.account;

import common.money.MonetaryAmount;
import common.money.Percentage;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import rewards.AccountContribution;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for the Account class that verify Account behavior works in isolation.
 */
public class AccountTests {

    private Account account;

    /**
     * Test account contructor with all fields, no credit cards
     */
    @Test
    public void testAccountConstructorFromFields0CreditCards() {
        account = new Account(AccountTestData.TEST_NUMBER_0, AccountTestData.TEST_NAME_0, AccountTestData.TEST_DOB_0,
                              AccountTestData.TEST_EMAIL_0, AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                              AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0);

        AccountTestData.verifyAccount(account, null,
                                      AccountTestData.TEST_NUMBER_0,
                                      AccountTestData.TEST_NAME_0,
                                      AccountTestData.TEST_DOB_0,
                                      AccountTestData.TEST_EMAIL_0, AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0, 0);
    }

    /**
     * Test account contructor with all fields, one credit card
     */
    @Test
    public void testAccountConstructorFromFields1CreditCard() {
        account = new Account(AccountTestData.TEST_NUMBER_0, AccountTestData.TEST_NAME_0, AccountTestData.TEST_DOB_0,
                              AccountTestData.TEST_EMAIL_0, AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                              AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                              new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));

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
     * Test account contructor with all fields, two credit cards
     */
    @Test
    public void testAccountConstructorFromFields2CreditCards() {
        account = new Account(AccountTestData.TEST_NUMBER_0, AccountTestData.TEST_NAME_0, AccountTestData.TEST_DOB_0,
                              AccountTestData.TEST_EMAIL_0, AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                              AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                              new CreditCard(AccountTestData.TEST_CREDIT_CARD_0),
                              new CreditCard(AccountTestData.TEST_CREDIT_CARD_1));

        AccountTestData.verifyAccount(account, null,
                                      AccountTestData.TEST_NUMBER_0,
                                      AccountTestData.TEST_NAME_0,
                                      AccountTestData.TEST_DOB_0, AccountTestData.TEST_EMAIL_0,
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0, 0,
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_0),
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_1));
    }

    /**
     * Test setBeneficiaries
     */
    @Test
    public void testSetBeneficiaries() {
        Account account =
                new Account(AccountTestData.TEST_NUMBER_0, AccountTestData.TEST_NAME_0, AccountTestData.TEST_DOB_0,
                            AccountTestData.TEST_EMAIL_0, AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                            AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                            new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));

        account.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_0);

        Set<Beneficiary> expectedBeneficiarySet = new HashSet<Beneficiary>();
        expectedBeneficiarySet.add(new Beneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                   AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                   AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0));
        expectedBeneficiarySet.add(new Beneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                                                   AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                                                   AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1));

        account.setBeneficiaries(expectedBeneficiarySet);

        AccountTestData.verifyAccount(account, null,
                                      AccountTestData.TEST_NUMBER_0,
                                      AccountTestData.TEST_NAME_0,
                                      AccountTestData.TEST_DOB_0,
                                      AccountTestData.TEST_EMAIL_0,
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0, 2,
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));

        Assert.assertEquals("beneficiaries", expectedBeneficiarySet, account.getBeneficiaries());
    }

    /**
     * Test setCreditCards
     */
    @Test
    public void testSetCreditCards() {
        Account account =
                new Account(AccountTestData.TEST_NUMBER_0, AccountTestData.TEST_NAME_0, AccountTestData.TEST_DOB_0,
                            AccountTestData.TEST_EMAIL_0, AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                            AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                            new CreditCard(AccountTestData.TEST_CREDIT_CARD_UNUSED));

        Set<CreditCard> expectedCreditCards = new HashSet<CreditCard>();
        expectedCreditCards.add(new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));
        expectedCreditCards.add(new CreditCard(AccountTestData.TEST_CREDIT_CARD_1));

        account.setCreditCards(expectedCreditCards);

        AccountTestData.verifyAccount(account, null,
                                      AccountTestData.TEST_NUMBER_0,
                                      AccountTestData.TEST_NAME_0,
                                      AccountTestData.TEST_DOB_0,
                                      AccountTestData.TEST_EMAIL_0,
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0, 0,
                                      expectedCreditCards);
        Assert.assertNotSame("creditCard sets diffferent objects", expectedCreditCards, account.getCreditCards());

    }

    /**
     * Test getCreditCardNumbers, no credit cards
     */
    @Test
    public void testGetCreditCardNumbers0CreditCards() {
        account = new Account(AccountTestData.TEST_NUMBER_0, AccountTestData.TEST_NAME_0, AccountTestData.TEST_DOB_0,
                              AccountTestData.TEST_EMAIL_0, AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                              AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0);

        final Set<String> expectedCreditCardNumbers = new HashSet<String>();

        Assert.assertEquals("credit card numbers", expectedCreditCardNumbers, account.getCreditCardNumbers());
    }

    /**
     * Test getCreditCardNumbers, one credit cards
     */
    @Test
    public void testGetCreditCardNumbers1CreditCard() {
        account = new Account(AccountTestData.TEST_NUMBER_0, AccountTestData.TEST_NAME_0, AccountTestData.TEST_DOB_0,
                              AccountTestData.TEST_EMAIL_0, AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                              AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                              new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));

        final Set<String> expectedCreditCardNumbers = new HashSet<String>();
        expectedCreditCardNumbers.add(AccountTestData.TEST_CREDIT_CARD_0);

        Assert.assertEquals("credit card numbers", expectedCreditCardNumbers, account.getCreditCardNumbers());
    }

    /**
     * Test getCreditCardNumbers, two credit cards
     */
    @Test
    public void testGetCreditCardNumbers2CreditCards() {
        account = new Account(AccountTestData.TEST_NUMBER_0, AccountTestData.TEST_NAME_0, AccountTestData.TEST_DOB_0,
                              AccountTestData.TEST_EMAIL_0, AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                              AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                              new CreditCard(AccountTestData.TEST_CREDIT_CARD_0),
                              new CreditCard(AccountTestData.TEST_CREDIT_CARD_1));

        final Set<String> expectedCreditCardNumbers = new HashSet<String>();
        expectedCreditCardNumbers.add(AccountTestData.TEST_CREDIT_CARD_0);
        expectedCreditCardNumbers.add(AccountTestData.TEST_CREDIT_CARD_1);

        Assert.assertEquals("credit card numbers", expectedCreditCardNumbers, account.getCreditCardNumbers());
    }


    /**
     * Test isValid for 100% allocation total
     */
    @Test
    public void testAccountIsValid() {
        createAccount0();
        Assert.assertTrue(account.isValid());
    }

    /**
     * Test isValid with 0 beneficiaries
     */
    @Test
    public void testAccountIsValidWithNoBeneficiaries() {
        createAccount0();
        account.removeBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_0);
        account.removeBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_1);
        Assert.assertTrue(account.isValid());
    }


    /**
     * Test isValid for >100% allocation total
     */
    @Test
    public void testAccountIsInvalidWhenBeneficiaryAllocationsAreOver100() {
        createAccount0();
        account.addBeneficiary("Billy", new Percentage(0.1));
        Assert.assertFalse(account.isValid());
    }


    /**
     * Test isValid for <100% allocation total
     */
    @Test
    public void testAccountIsValidWhenBeneficiaryAllocationsAreUnder100() {
        createAccount0();
        account.getBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_1).setAllocationPercentage(new Percentage(0.4));
        Assert.assertTrue(account.isValid());
    }


    /**
     * Test makeContribution
     */
    @Test
    public void testMakeContribution() {
        createAccount0();
        MonetaryAmount amount10 = new MonetaryAmount(10);
        MonetaryAmount amount5 = new MonetaryAmount(5);
        AccountContribution contribution = account.makeContribution(amount10);
        Assert.assertEquals("contribution amount", amount10, contribution.getAmount());
        Assert.assertEquals("amount for " + AccountTestData.TEST_BENEFICIARY_NAME_0_0, amount5,
                            contribution.getDistribution(AccountTestData.TEST_BENEFICIARY_NAME_0_0).getAmount());
        Assert.assertEquals("amount for " + AccountTestData.TEST_BENEFICIARY_NAME_0_1, amount5,
                            contribution.getDistribution(AccountTestData.TEST_BENEFICIARY_NAME_0_1).getAmount());
    }


    /**
     * Test getBeneficiary for first beneficiary (of two)
     */
    @Test
    public void testGetBeneficiary0() {
        createAccount0();
        Beneficiary beneficiary = account.getBeneficiary("Annabelle");
        Assert.assertNotNull("non-null beneficiary returned", beneficiary);
        Assert.assertEquals("beneficiary name", AccountTestData.TEST_BENEFICIARY_NAME_0_0, beneficiary.getName());
        Assert.assertEquals("beneficiary percentage", AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                            beneficiary.getAllocationPercentage());
        Assert.assertEquals("beneficiary savings", AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0,
                            beneficiary.getSavings());

    }


    /**
     * Test getBeneficiary for second beneficiary (of two)
     */
    @Test
    public void testGetBeneficiary1() {
        createAccount0();
        Beneficiary beneficiary = account.getBeneficiary("Corgan");
        Assert.assertNotNull("non-null beneficiary returned", beneficiary);
        Assert.assertEquals("beneficiary name", AccountTestData.TEST_BENEFICIARY_NAME_0_1, beneficiary.getName());
        Assert.assertEquals("beneficiary percentage", AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                            beneficiary.getAllocationPercentage());
        Assert.assertEquals("beneficiary savings", AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1,
                            beneficiary.getSavings());

    }


    /**
     * Test getBeneficiary for non-existent name
     */
    @Test
    public void testGetBeneficiaryNonExistent() {
        createAccount0();
        final String badName = "Cookoo";
        try {
            Beneficiary beneficiary = account.getBeneficiary(badName);
            Assert.fail("should get EmptyResultDataAccessException for non-existent name '" + badName + "':account=" +
                        account);
        } catch (EmptyResultDataAccessException ex) {
            final String beneficiaryInfo = "beneficiaryName='" + badName + "'";
            Assert.assertTrue("exception message should contain beneficiary name:" + beneficiaryInfo + ":message=" +
                              ex.getMessage(),
                              ex.getMessage().contains(beneficiaryInfo));
        }
    }


    /**
     * Test addBeneficiary with name only
     */
    @Test
    public void testAddBeneficiaryNameOnly0() {
        createAccount0();
        addBeneficiaryWithPercentage("Addy", null);
    }


    /**
     * Test addBeneficiary with name only for two successive beneficiaries
     */
    @Test
    public void testAddBeneficiaryNameOnly0and1() {
        createAccount0();
        addBeneficiaryWithPercentage("Addy", null);
        addBeneficiaryWithPercentage("Bertrum", null);
    }


    /**
     * Test addBeneficiary with name only, duplicate name
     */
    @Test
    public void testAddBeneficiaryNameOnlyDuplicateName() {
        createAccount0();
        try {
            account.addBeneficiary("Annabelle");
            Assert.fail("expecting DuplicateBeneficiaryNameException exception");
        } catch (DuplicateBeneficiaryNameException ex) {
            final String nameMsg = "name=Annabelle";
            Assert.assertTrue("exception message should include:" + nameMsg, ex.getMessage().contains(nameMsg));
        }
    }


    /**
     * Test addBeneficiary with name and percentage
     */
    @Test
    public void testAddBeneficiaryNameAndPercentage0() {
        createAccount0();
        addBeneficiaryWithPercentage("Addy", Percentage.oneHundred());
    }


    /**
     * Test addBeneficiary with name and percentage for two successive beneficiaries
     */
    @Test
    public void testAddBeneficiaryNameAndPercentage0and1() {
        createAccount0();
        addBeneficiaryWithPercentage("Addy", new Percentage(0.5));
        addBeneficiaryWithPercentage("Bertrum", new Percentage(0.25));
    }

    /**
     * Test addBeneficiary with name and percentage, duplicate name
     */
    @Test
    public void testAddBeneficiaryNameAndPercentageDuplicateName() {
        createAccount0();
        try {
            account.addBeneficiary("Annabelle", Percentage.oneHundred());
            Assert.fail("expecting DuplicateBeneficiaryNameException exception");
        } catch (DuplicateBeneficiaryNameException ex) {
            final String nameMsg = "beneficiaryName='Annabelle'";
            Assert.assertTrue("exception message \"" + ex.getMessage() + "\" should include:" + nameMsg,
                              ex.getMessage().contains(nameMsg));
        }
    }


    /**
     * Test getBeneficiary for first beneficiary (of two)
     */
    @Test
    public void testRemoveBeneficiary0() {
        createAccount0();

        final String beneficiaryName = "Annabelle";
        verifyRemoveBeneficiary(beneficiaryName);
    }


    /**
     * Test getBeneficiary for second beneficiary (of two)
     */
    @Test
    public void testRemoveBeneficiary1() {
        createAccount0();

        final String beneficiaryName = "Corgan";
        verifyRemoveBeneficiary(beneficiaryName);
    }

    /**
     * Test getBeneficiary for non-existent name
     */
    @Test
    public void testRemoveBeneficiaryNonExistent() {
        createAccount0();

        final String badName = "Cookoo";
        try {
            account.removeBeneficiary(badName);
            Assert.fail("should get EmptyResultDataAccessException for non-existent name '" + badName + "':account=" +
                        account);
        } catch (EmptyResultDataAccessException ex) {
            final String beneficiaryInfo = "beneficiaryName='" + badName + "'";
            Assert.assertTrue("exception message should contain beneficiary name:" + beneficiaryInfo,
                              ex.getMessage().contains(beneficiaryInfo));
        }
    }


    /**
     * Test hasCreditCardInfo
     */
    @Test
    public void testHasCreditCardCard() {
        createAccount0();

        Assert.assertTrue("should have creditCard:" + AccountTestData.TEST_CREDIT_CARD_0,
                          account.hasCreditCard(AccountTestData.TEST_CREDIT_CARD_0));
        Assert.assertFalse("shouldn't have creditCard:" + AccountTestData.TEST_CREDIT_CARD_1,
                           account.hasCreditCard(AccountTestData.TEST_CREDIT_CARD_1));
    }

    /**
     * Test addCreditCard to empty account
     */
    @Test
    public void testAddCreditCard0() {
        account = new Account(AccountTestData.TEST_NUMBER_0, AccountTestData.TEST_NAME_0, AccountTestData.TEST_DOB_0,
                              AccountTestData.TEST_EMAIL_0, AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                              AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0);

        account.addCreditCard(new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));

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
     * Test addCreditCard (2 cards)
     */
    @Test
    public void testAddCreditCard0and1() {
        account = new Account(AccountTestData.TEST_NUMBER_0, AccountTestData.TEST_NAME_0, AccountTestData.TEST_DOB_0,
                              AccountTestData.TEST_EMAIL_0, AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                              AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0);

        account.addCreditCard(new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));
        account.addCreditCard(new CreditCard(AccountTestData.TEST_CREDIT_CARD_1));

        AccountTestData.verifyAccount(account, null, AccountTestData.TEST_NUMBER_0,
                                      AccountTestData.TEST_NAME_0,
                                      AccountTestData.TEST_DOB_0,
                                      AccountTestData.TEST_EMAIL_0,
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0, 0,
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_0),
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_1));

    }

    /**
     * Test addCreditCard with duplicate card
     */
    @Test
    public void testAddCreditCardDuplicateCard() {
        createAccount0();

        CreditCard creditCard = new CreditCard(AccountTestData.TEST_CREDIT_CARD_0);
        try {
            account.addCreditCard(creditCard);
            Assert.fail("expecting DuplicateAccountCreditCards exception");
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertFalse("no duplicate name", ex.hasDuplicateAccountName());
            Assert.assertFalse("no duplicate number", ex.hasDuplicateAccountNumber());
            Assert.assertTrue("has duplicate credit cards", ex.hasDuplicateCreditCardNumbers());
            Assert.assertEquals("duplicate credit card numbers",
                                Collections.singleton(AccountTestData.TEST_CREDIT_CARD_0),
                                ex.getDuplicateCreditCardNumbers());
        }

    }

    /**
     * Test removeCreditCard (only card)
     */
    @Test
    public void testRemoveCreditCardOnlyCard() {
        createAccount0();

        Set<CreditCard> expectedCreditCards = new HashSet<CreditCard>();

        account.removeCreditCard(AccountTestData.TEST_CREDIT_CARD_0);

        AccountTestData.verifyAccount(account, 0,
                                      AccountTestData.TEST_NUMBER_0,
                                      AccountTestData.TEST_NAME_0,
                                      AccountTestData.TEST_DOB_0,
                                      AccountTestData.TEST_EMAIL_0,
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0, 2);

    }

    /**
     * Test removeCreditCard (one of two cards)
     */
    @Test
    public void testRemoveCreditCardOneOfTwo() {
        account = new Account(AccountTestData.TEST_NUMBER_0, AccountTestData.TEST_NAME_0, AccountTestData.TEST_DOB_0,
                              AccountTestData.TEST_EMAIL_0, AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                              AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0,
                              new CreditCard(AccountTestData.TEST_CREDIT_CARD_0),
                              new CreditCard(AccountTestData.TEST_CREDIT_CARD_1));

        account.removeCreditCard(AccountTestData.TEST_CREDIT_CARD_0);

        AccountTestData.verifyAccount(account, null,
                                      AccountTestData.TEST_NUMBER_0,
                                      AccountTestData.TEST_NAME_0,
                                      AccountTestData.TEST_DOB_0,
                                      AccountTestData.TEST_EMAIL_0,
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0, 0,
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_1));
    }

    /**
     * Test removeCreditCard for card not on account
     */
    @Test
    public void testRemoveCreditCardNotOnAccount() {
        createAccount0();

        final String creditCardNumber = AccountTestData.TEST_CREDIT_CARD_1;
        try {
            account.removeCreditCard(creditCardNumber);
            Assert.fail(
                    "should get EmptyResultDataAccessException for non-existent creditCardNumber '" +
                    creditCardNumber +
                    "':account=" +
                    account);
        } catch (EmptyResultDataAccessException ex) {
            final String nameMsg = "number='" + creditCardNumber + "'";
            Assert.assertTrue("exception message \"" + ex.getMessage() + "\" should include:" + nameMsg,
                              ex.getMessage().contains(nameMsg));
        }
    }


    /**
     * Test equals (same object)
     */
    @Test
    public void testEqualsWhenSame() {
        createAccount0();

        Assert.assertEquals("same object should be equal", account, account);
    }

    /**
     * Test equals (same values including ID)
     */
    @Test
    public void testEqualsWhenEqualIncludingId() {
        createAccount0();

        Account otherAccount = AccountTestData.createTestAccount0();
        Assert.assertTrue("same values should be equal:account=" + account + ":otherAccount=" + otherAccount,
                          account.equals(otherAccount));
    }

    /**
     * Test equals (same values excluding ID)
     */
    @Test
    public void testEqualsWhenEqualExcludingId() {
        createAccount0();

        Account otherAccount = AccountTestData.createTestAccount0();
        otherAccount.setEntityId(null);
        Assert.assertTrue(
                "same values except for ID should be equal:account=" + account + ":otherAccount=" + otherAccount,
                account.equals(otherAccount));
        Assert.assertTrue("same values except for ID should be equal (reversed)", otherAccount.equals(account));
    }

    /**
     * Test (not) equals (null)
     */
    @Test
    public void testNotEqualsWhenOtherNull() {
        createAccount0();

        Assert.assertFalse("null should not be equal", account.equals(null));
    }

    /**
     * Test (not) equals (different name)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentName() {
        createAccount0();

        Account otherAccount = AccountTestData.createTestAccount0();
        otherAccount.setName(AccountTestData.TEST_NAME_1);
        Assert.assertFalse("different name should not be equal", account.equals(otherAccount));
    }

    /**
     * Test (not) equals (different number)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentNumber() {
        createAccount0();

        Account otherAccount = AccountTestData.createTestAccount0();
        otherAccount.setNumber(AccountTestData.TEST_NUMBER_1);
        Assert.assertFalse("different number should not be equal", account.equals(otherAccount));
    }

    /**
     * Test (not) equals (different DoB)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentDateOfBirth() {
        createAccount0();

        Account otherAccount = AccountTestData.createTestAccount0();
        otherAccount.setDateOfBirth(AccountTestData.TEST_DOB_1);
        Assert.assertFalse("different DoB should not be equal", account.equals(otherAccount));
    }

    /**
     * Test (not) equals (different email)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentEMail() {
        createAccount0();

        Account otherAccount = AccountTestData.createTestAccount0();
        otherAccount.setEmail(AccountTestData.TEST_EMAIL_1);
        Assert.assertFalse("different email should not be equal", account.equals(otherAccount));
    }

    /**
     * Test (not) equals (different receive newsletter)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentReceiveNewsletter() {
        createAccount0();

        Account otherAccount = AccountTestData.createTestAccount0();
        otherAccount.setReceiveNewsletter(!AccountTestData.TEST_RECEIVE_NEWSLETTER_0);
        Assert.assertFalse("different receive newsletter should not be equal", account.equals(otherAccount));
    }

    /**
     * Test (not) equals (different receive email)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentReceiveEMail() {
        createAccount0();

        Account otherAccount = AccountTestData.createTestAccount0();
        otherAccount.setReceiveMonthlyEmailUpdate(!AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0);
        Assert.assertFalse("different receive email should not be equal", account.equals(otherAccount));
    }

    /**
     * Test (not) equals (different credit cards)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentCreditCards() {
        createAccount0();

        Account otherAccount = AccountTestData.createTestAccount0();
        Set<CreditCard> creditCards = new HashSet<CreditCard>(otherAccount.getCreditCards());
        creditCards.add(new CreditCard(AccountTestData.TEST_CREDIT_CARD_1));
        otherAccount.setCreditCards(creditCards);
        Assert.assertFalse("different credit cards should not be equal", account.equals(otherAccount));
    }

    /**
     * Test (not) equals (different beneficiaries)
     */
    @Test
    public void testNotEqualsWhenOtherDifferentBeneficiaries() {
        createAccount0();

        Account otherAccount = AccountTestData.createTestAccount0();
        otherAccount.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_0);
        Assert.assertFalse("different beneficiaries should not be equal", account.equals(otherAccount));
    }

    /**
     * Test Jackson Json marshalling/unmarshalling for annotations in class What
     * goes in should come out!
     */
    @Test
    public void testJsonMarshallUnmarshall() throws Exception {
        createAccount0();

        ObjectMapper mapper = new ObjectMapper();
        String marshalledVersion = mapper
                .writeValueAsString(account);

        Account unmarshalledAccount = mapper.readValue(
                marshalledVersion, Account.class);
        Assert.assertEquals("original and unmarshalled account",
                            account, unmarshalledAccount);
    }

    /**
     * Helper to set up account with a valid set of beneficiaries to prepare for testing
     */
    private void createAccount0() {
        account = AccountTestData.createTestAccount0();
    }


    /**
     * Helper for adding beneficiary with name and optional percentage
     *
     * @param beneficiaryName
     * @param allocationPercentage
     * @return expectedBeneficiaries
     */
    private void addBeneficiaryWithPercentage(String beneficiaryName, Percentage allocationPercentage) {
        Set<Beneficiary> expectedBeneficiaries = new HashSet<Beneficiary>(account.getBeneficiaries());
        if (allocationPercentage == null) {
            Beneficiary newBeneficiary = new Beneficiary(beneficiaryName, Percentage.zero());
            expectedBeneficiaries.add(newBeneficiary);
            account.addBeneficiary(beneficiaryName);
        } else {
            Beneficiary newBeneficiary = new Beneficiary(beneficiaryName, allocationPercentage);
            expectedBeneficiaries.add(newBeneficiary);
            account.addBeneficiary(beneficiaryName, allocationPercentage);
        }

        Assert.assertEquals("beneficiaries", expectedBeneficiaries, account.getBeneficiaries());
    }

    /**
     * Helper for testRemoveBeneficiary*
     *
     * @param beneficiaryName
     */
    private void verifyRemoveBeneficiary(String beneficiaryName) {
        Set<Beneficiary> expectedBeneficiaries = new HashSet<Beneficiary>(account.getBeneficiaries());
        for (Beneficiary b : expectedBeneficiaries) {
            if (b.getName().equals(beneficiaryName)) {
                expectedBeneficiaries.remove(b);
                break;
            }
        }
        account.removeBeneficiary(beneficiaryName);
        Assert.assertEquals("beneficiaries", expectedBeneficiaries, account.getBeneficiaries());
    }

}
