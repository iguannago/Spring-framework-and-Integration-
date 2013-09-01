package rewards.internal.account;

import common.datetime.SimpleDate;
import common.money.MonetaryAmount;
import common.money.Percentage;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Unit test for the Hibernate-based account repository implementation.
 * Tests application data access behavior to verify
 * the Account Hibernate mapping and queries are correct.
 */
public class HibernateAccountRepositoryTests {

    private HibernateAccountRepository repository;

    private JdbcTemplate jdbcTemplate;

    private PlatformTransactionManager transactionManager;

    private TransactionStatus transactionStatus;
    private SessionFactory sessionFactory;


    @Before
    public void setUp() throws Exception {
        jdbcTemplate = new JdbcTemplate(createTestDataSource());
        sessionFactory = createTestSessionFactory(jdbcTemplate.getDataSource());
        repository = new HibernateAccountRepository(sessionFactory);
        transactionManager = new HibernateTransactionManager(sessionFactory);
    }

    /**
     * Verify we get all expected accounts
     */
    @Test
    public void testGetAllAccounts() {
        createTransactionStatus(true);
        List<Account> accounts = repository.getAllAccounts();
        Assert.assertEquals("Wrong number of accounts", getAccountsCount(), accounts.size());
    }

    /**
     * Verify we get expected account
     */
    @Test
    public void testGetAccount() {
        createTransactionStatus(true);
        Account account = repository.getAccount(0);
        // assert the returned account contains what you expect given the state
        // of the database
        verifyAccount0(account);
    }

    /**
     * Account with one credit card, 2 beneficaries
     */
    @Test
    public void testFindByAccountNumber0() {
        createTransactionStatus(true);
        Account account = repository.findByAccountNumber("123456789");
        // assert the returned account contains what you expect given the state of the database
        // and the Account Hibernate mapping configuration
        verifyAccount0(account);
    }

    /**
     * Account with one credit card, 2 beneficaries
     */
    @Test
    public void testFindByAccountNumber1() {
        createTransactionStatus(true);
        Account account = repository.findByAccountNumber("123456001");
        // assert the returned account contains what you expect given the state of the database
        // and the Account Hibernate mapping configuration
        verifyAccount1(account);
    }

    /**
     * Account with one credit card, 2 beneficaries
     */
    @Test
    public void testFindByCreditCard0() {
        createTransactionStatus(true);
        Account account = repository.findByCreditCard("1234123412341234");
        // assert the returned account contains what you expect given the state of the database
        // and the Account Hibernate mapping configuration
        verifyAccount0(account);
    }


    /**
     * Account with one credit card, 0 beneficaries
     */
    @Test
    public void testFindByCreditCard1() {
        createTransactionStatus(true);
        Account account = repository.findByCreditCard("1234123412340001");
        // assert the returned account contains what you expect given the state of the database
        // and the Account Hibernate mapping configuration
        verifyAccount1(account);

    }


    /**
     * Account with two credit cards, 3 beneficiaries, find with first card
     */
    @Test
    public void testFindByCreditCard4UseFirstCard() {
        createTransactionStatus(true);
        Account account = repository.findByCreditCard("1234123412340004");
        // assert the returned account contains what you expect given the state of the database
        // and the Account Hibernate mapping configuration
        verifyAccount4(account);
    }


    /**
     * Account with two credit cards, 3 beneficiaries, find with second card
     */
    @Test
    public void testFindByCreditCard4UseSecondCard() {
        createTransactionStatus(true);
        Account account = repository.findByCreditCard("4320123412340005");
        // assert the returned account contains what you expect given the state of the database
        // and the Account Hibernate mapping configuration
        verifyAccount4(account);
    }


    /**
     * Test hasNoAccountWithName for name that will not be there
     */
    @Test
    public void testIsAccountNameAvailableForCreateNameUnused() {
        createTransactionStatus(true);
        final String nameToTest = AccountTestData.TEST_NAME_UNUSED;
        Assert.assertTrue("should be available " + nameToTest,
                          repository.hasNoAccountWithName(nameToTest));
    }

    /**
     * Test hasNoAccountWithName for name that will already be there
     */
    @Test
    public void testhasNoAccountWithNameNameInUse() {
        createTransactionStatus(true);
        final String nameToTest = AccountTestData.TEST_NAME_0;
        Assert.assertFalse("should not be available " + nameToTest,
                           repository.hasNoAccountWithName(nameToTest));
    }

    /**
     * Test hasNoAccountWithNumber for number that will be there
     */
    @Test
    public void testHasNoAccountWithNumberNumberUnused() {
        createTransactionStatus(true);
        final String numberToTest = AccountTestData.TEST_NUMBER_UNUSED;
        Assert.assertTrue("should be available " + numberToTest,
                          repository.hasNoAccountWithNumber(numberToTest));
    }

    /**
     * Test hasNoAccountWithNumber for number that will not be there
     */
    @Test
    public void testHasNoAccountWithNumberNumberInUse() {
        createTransactionStatus(true);
        final String numberToTest = AccountTestData.TEST_NUMBER_0;
        Assert.assertFalse("should not be available " + numberToTest,
                           repository.hasNoAccountWithNumber(numberToTest));
    }

    /**
     * Test creation of valid account
     */
    @Test
    public void testCreateAccount() {
        createTransactionStatus(false);
        Account account = AccountTestData.createTestAccount1();
        repository.create(account);
        Integer entityId = account.getEntityId();
        Assert.assertNotNull("created account entityId", entityId);

        commitTransaction();

        createTransactionStatus(true);
        Account createdAccount = repository.getAccount(entityId);

        Assert.assertNotNull("created account", createdAccount);
        AccountTestData.verifyAccount(account, entityId,
                                      AccountTestData.TEST_NUMBER_1,
                                      AccountTestData.TEST_NAME_1,
                                      AccountTestData.TEST_DOB_1,
                                      AccountTestData.TEST_EMAIL_1,
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_1,
                                      AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1, 0,
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_1));
    }


    /**
     * Test creation attempt for duplicate account name
     */
    @Test(expected = ConstraintViolationException.class)
    public void testCreateAccountDuplicateName() {
        createTransactionStatus(false);
        Account account = AccountTestData.createTestAccount1();
        account.setName(AccountTestData.TEST_NAME_0);
        repository.create(account);
    }

    /**
     * Test creation attempt for duplicate account number
     */
    @Test(expected = ConstraintViolationException.class)
    public void testCreateAccountDuplicateNumber() {
        createTransactionStatus(false);
        Account account = AccountTestData.createTestAccount1();
        account.setNumber(AccountTestData.TEST_NUMBER_0);
        repository.create(account);
    }

    /**
     * Test creation attempt for duplicate account name and number
     */
    @Test(expected = ConstraintViolationException.class)
    public void testCreateAccountDuplicateNameAndNumber() {
        createTransactionStatus(false);
        Account account = AccountTestData.createTestAccount1();
        account.setName(AccountTestData.TEST_NAME_0);
        account.setNumber(AccountTestData.TEST_NUMBER_0);
        repository.create(account);
    }

    /**
     * Test updateAccountInfo (including commit, so worth doing even if the DAO does nothing!)
     */
    @Test
    public void testUpdateAccount() {
        createTransactionStatus(false);
        Account account = repository.getAccount(0);
        account.setName(AccountTestData.TEST_NAME_UNUSED);
        account.setReceiveMonthlyEmailUpdate(!AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0);
        Set<CreditCard> creditCards = new HashSet<CreditCard>(account.getCreditCards());
        creditCards.add(new CreditCard(AccountTestData.TEST_CREDIT_CARD_UNUSED));
        account.setCreditCards(creditCards);

        repository.update(account);
        commitTransaction();

        createTransactionStatus(true);
        Account updatedAccount = repository.getAccount(0);

        Assert.assertNotNull("updated account", updatedAccount);
        AccountTestData.verifyAccount(updatedAccount, 0,
                                      AccountTestData.TEST_NUMBER_0,
                                      AccountTestData.TEST_NAME_UNUSED,
                                      AccountTestData.TEST_DOB_0,
                                      AccountTestData.TEST_EMAIL_0,
                                      AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                      !AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0, 2,
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_0),
                                      new CreditCard(AccountTestData.TEST_CREDIT_CARD_UNUSED));
    }

    /**
     * Test updateAccountInfo with duplicate name (including commit, so worth doing even if the DAO does nothing!)
     */
    @Test(expected = DataIntegrityViolationException.class)
    public void testUpdateAccountDuplicateName() {
        createTransactionStatus(false);
        Account account = repository.getAccount(0);
        account.setName("Dollie R. Adams");

        repository.update(account);
        commitTransaction();
    }

    /**
     * Test hasNoAccountWithCreditCard for unused card
     */
    @Test
    public void testHasNoAccountWithCreditCardCardUnused() {
        createTransactionStatus(true);
        final String cardToTest = AccountTestData.TEST_CREDIT_CARD_UNUSED;
        Assert.assertTrue("should be available (unused) " + cardToTest,
                          repository.hasNoAccountWithCreditCard(cardToTest));
    }

    /**
     * Test hasNoAccountWithCreditCard for card that will be there
     */
    @Test
    public void testHasNoAccountWithCreditCardCardInUse() {
        createTransactionStatus(true);
        final String cardToTest = AccountTestData.TEST_NAME_UNUSED;
        Assert.assertTrue("should be available (unused) " + cardToTest,
                          repository.hasNoAccountWithCreditCard(cardToTest));
    }

    /**
     * Roll-back transaction if it's not been committed
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        if (!transactionStatus.isCompleted()) {
            transactionManager.rollback(transactionStatus);
        }
    }

    /**
     * Helper verifying account against account0
     *
     * @param account
     */
    private void verifyAccount0(Account account) {
        AccountTestData.verifyAccount(account, 0,
                                      "123456789", "Keith and Keri Donald",
                                      new SimpleDate(4, 11, 1981).asDate(), "keithd@gmail.com", true, false, 2,
                                      new CreditCard("1234123412341234"));

        Beneficiary b1 = account.getBeneficiary("Annabelle");
        Assert.assertNotNull("Annabelle should be a beneficiary", b1);
        Assert.assertEquals("Annabelle  allocation percentage", AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                            b1.getAllocationPercentage());
        Assert.assertEquals("Annabelle savings", AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0, b1.getSavings());

        Beneficiary b2 = account.getBeneficiary("Corgan");
        Assert.assertNotNull("Corgan should be a beneficiary", b2);
        Assert.assertEquals("Corgan allocation percentage", AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                            b2.getAllocationPercentage());
        Assert.assertEquals("Corgan savings", AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1, b2.getSavings());
    }


    /**
     * Helper verifying account against account1
     *
     * @param account
     */
    private void verifyAccount1(Account account) {
        AccountTestData.verifyAccount(account, 1,
                                      "123456001", "Dollie R. Adams",
                                      new SimpleDate(4, 14, 1973).asDate(), "dollie@hotmail.com", false, true, 0,
                                      new CreditCard("1234123412340001"));
    }

    /**
     * helper for common validation of account 4
     *
     * @param account
     */
    private void verifyAccount4(Account account) {
        AccountTestData.verifyAccount(account, 4,
                                      "123456004", "Chad I. Cobbs",
                                      new SimpleDate(4, 23, 1976).asDate(), "chadc@netzero.com", true, false, 3,
                                      new CreditCard("1234123412340004"), new CreditCard("4320123412340005"));

        final Percentage percent50 = new Percentage(0.5);
        final Percentage percent25 = new Percentage(0.25);

        Beneficiary b1 = account.getBeneficiary("Jane");
        Assert.assertNotNull("Jane should be a beneficiary", b1);
        Assert.assertEquals("Jane savings", MonetaryAmount.zero(), b1.getSavings());
        Assert.assertEquals("Jane allocation percentage", percent25, b1.getAllocationPercentage());

        Beneficiary b2 = account.getBeneficiary("Amy");
        Assert.assertNotNull("Amy should be a beneficiary", b2);
        Assert.assertEquals("Amy savings", MonetaryAmount.zero(), b2.getSavings());
        Assert.assertEquals("Amy allocation percentage", percent25, b2.getAllocationPercentage());

        Beneficiary b3 = account.getBeneficiary("Susan");
        Assert.assertNotNull("Susan should be a beneficiary", b3);
        Assert.assertEquals("Susan savings", MonetaryAmount.zero(), b3.getSavings());
        Assert.assertEquals("Susan allocation percentage", percent50, b3.getAllocationPercentage());
    }


    /**
     * Helper creating dataSource
     *
     * @return dataSource
     */
    private DataSource createTestDataSource() {
        return new EmbeddedDatabaseBuilder().setName("rewards")
                                            .addScript("/rewards/testdb/schema.sql")
                                            .addScript("/rewards/testdb/test-data.sql")
                                            .addScript("/rewards/testdb/test-data-additional-cards.sql").build();
    }

    /**
     * Helper creating session factory
     *
     * @param dataSource
     * @return resulting session factory
     * @throws Exception
     */
    private SessionFactory createTestSessionFactory(DataSource dataSource) throws Exception {
        // createAccountInfo a FactoryBean to help createAccountInfo a Hibernate SessionFactory
        AnnotationSessionFactoryBean factoryBean = new AnnotationSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setAnnotatedClasses(new Class[]{
                Account.class,
                Beneficiary.class, CreditCard.class
        });
        factoryBean.setHibernateProperties(createHibernateProperties());
        // initialize according to the Spring InitializingBean contract
        factoryBean.afterPropertiesSet();
        // get the created session factory
        return (SessionFactory) factoryBean.getObject();
    }

    private Properties createHibernateProperties() {
        Properties properties = new Properties();
        // turn on formatted SQL logging (very useful to verify Hibernate is
        // issuing proper SQL)
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");
        return properties;
    }


    /**
     * Create transaction
     */
    private void createTransactionStatus(boolean readOnly) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setReadOnly(readOnly);
        transactionStatus = transactionManager.getTransaction(definition);
    }

    /**
     * Commit transaction
     */
    private void commitTransaction() {
        transactionManager.commit(transactionStatus);
    }

    /**
     * Get count of accounts
     *
     * @return count
     */
    private int getAccountsCount() {
        return jdbcTemplate.queryForInt("SELECT COUNT(*) FROM T_ACCOUNT");
    }
}
