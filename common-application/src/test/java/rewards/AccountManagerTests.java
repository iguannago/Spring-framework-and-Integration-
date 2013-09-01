package rewards;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import rewards.internal.account.*;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Unit test for the account manager implementation. Tests
 * application behavior to verify the Account Hibernate mapping is correct.
 */
@ContextConfiguration("classpath:/rewards/common-application-test-config.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class AccountManagerTests {

    @Autowired
    private AccountManager accountManager;

    private JdbcTemplate jdbcTemplate;

    private TransactionTemplate readonlyTransactionTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * Method to set up JdbcTemplate
     */
    @Autowired
    public void initialize(DataSource dataSource, PlatformTransactionManager transactionManager) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setReadOnly(true);
        readonlyTransactionTemplate = new TransactionTemplate(transactionManager, definition);
    }


    /**
     * Verify we get all expected accounts
     */
    @Test
    public void testGetAllAccounts() {
        final List<Account> accounts = accountManager.getAllAccounts();

        readonlyTransactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                Assert.assertEquals("Wrong number of accounts", getAccountCount(), accounts.size());
                return null;
            }
        });

    }

    /**
     * Test get for account
     */
    @Test
    public void testGetAccount() {
        final Account account = accountManager.getAccount(0);

        // assert the returned account contains what you expect given the state
        // of the database
        Assert.assertNotNull("account should never be null", account);
        readonlyTransactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                Session session = sessionFactory.getCurrentSession();
                session.update(account);
                AccountTestData.verifyAccount0(account, 2);

                AccountTestData.verifyBeneficiary(account,
                                                  AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                  AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                  AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

                AccountTestData.verifyBeneficiary(account,
                                                  AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                                                  AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                                                  AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);

                return null;
            }
        });
    }

    /**
     * Test get for account with non-existent ID
     */
    @Test
    public void testGetAccountNonExistentAccount() {
        Integer nonExistentId = getNonExistentId();
        try {
            Account account = accountManager.getAccount(nonExistentId);
            Assert.fail("expected empty data result exception account=" + account);
        } catch (EmptyResultDataAccessException ex) {
            String exmsg = ex.getMessage();
            Assert.assertTrue("exception msg should include account id:" + exmsg,
                              exmsg.contains("id=" + nonExistentId));
        }
    }

    /**
     * Test get for account
     */
    @Test
    public void testGetAccountInfo() {
        final AccountInfo accountInfo = accountManager.getAccountInfo(0);

        // assert the returned accountInfo contains what you expect given the state
        // of the database
        Assert.assertNotNull("accountInfo should never be null", accountInfo);
        readonlyTransactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                AccountTestData.verifyAccountInfo0(accountInfo);
                return null;
            }
        });
    }

    /**
     * Test get for account DTO with non-existent ID
     */
    @Test
    public void testGetAccountInfoNonExistentAccount() {
        Integer nonExistentId = getNonExistentId();
        try {
            AccountInfo accountInfo = accountManager.getAccountInfo(nonExistentId);
            Assert.fail("expected empty data result exception accountInfo=" + accountInfo);
        } catch (EmptyResultDataAccessException ex) {
            String exmsg = ex.getMessage();
            Assert.assertTrue("exception msg should include account id:" + exmsg,
                              exmsg.contains("id=" + nonExistentId));
        }
    }


    /**
     * Verify update account and beneficiaries for freshly created/bound account
     */
    @Test
    @DirtiesContext
    public void testUpdateAccountNonPersistant() {
        final Account account = AccountTestData.createTestAccount0();

        prepareAccountForUpdate(account);

        accountManager.update(account);

        readonlyTransactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                Session session = sessionFactory.getCurrentSession();
                Account updatedAccount = (Account) session.get(Account.class, 0);

                AccountTestData.verifyAccount(updatedAccount, 0,
                                              AccountTestData.TEST_NUMBER_0,
                                              "Ben Hale",
                                              AccountTestData.TEST_DOB_0,
                                              AccountTestData.TEST_EMAIL_0,
                                              AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                              AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0, 2,
                                              new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));

                AccountTestData.verifyBeneficiary(updatedAccount,
                                                  AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                  AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_1,
                                                  AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

                AccountTestData.verifyBeneficiary(updatedAccount,
                                                  AccountTestData.TEST_BENEFICIARY_NAME_1_2,
                                                  AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_2,
                                                  AccountTestData.TEST_BENEFICIARY_SAVINGS_1_2);

                return null;
            }
        });
    }


    /**
     * Verify update account and beneficiaries for detached account (got)
     */
    @Test
    @DirtiesContext
    public void testUpdateAccountDetached() {
        final Account account = readonlyTransactionTemplate.execute(new TransactionCallback<Account>() {
            @Override public Account doInTransaction(TransactionStatus transactionStatus) {
                final Account storedAccount = (Account) sessionFactory.getCurrentSession().get(Account.class, 0);
                prepareAccountForUpdate(storedAccount);
                return storedAccount;
            }
        });

        accountManager.update(account);

        readonlyTransactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                Session session = sessionFactory.getCurrentSession();
                Account updatedAccount = (Account) session.get(Account.class, 0);

                AccountTestData.verifyAccount(updatedAccount, 0,
                                              AccountTestData.TEST_NUMBER_0,
                                              "Ben Hale",
                                              AccountTestData.TEST_DOB_0,
                                              AccountTestData.TEST_EMAIL_0,
                                              AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                              AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0, 2,
                                              new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));

                AccountTestData.verifyBeneficiary(updatedAccount,
                                                  AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                  AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_1,
                                                  AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

                AccountTestData.verifyBeneficiary(updatedAccount,
                                                  AccountTestData.TEST_BENEFICIARY_NAME_1_2,
                                                  AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_2,
                                                  AccountTestData.TEST_BENEFICIARY_SAVINGS_1_2);

                return null;
            }
        });
    }


    /**
     * Test update for non-existent account. We make transactional to ensure failure does not modify the DB.
     */
    @Test
    @Transactional
    public void testUpdateNonExistentAccount() {
        Integer nonExistentId = getNonExistentId();
        Account account = AccountTestData.createTestAccount1();
        account.setEntityId(nonExistentId);
        account.setName(AccountTestData.TEST_NAME_0);

        try {
            accountManager.update(account);
            Assert.fail("expected empty data result exception account=" + account);
        } catch (EmptyResultDataAccessException ex) {
            String exmsg = ex.getMessage();
            Assert.assertTrue("exception msg should include account id:" + exmsg,
                              exmsg.contains("id=" + nonExistentId));
        }
    }


    /**
     * Test update with duplicate name. We make transactional to ensure failure does not modify the DB.
     */
    @Test
    @DirtiesContext
    public void testUpdateDuplicateName() {
        final AccountInfo expectedAccountInfo = getOriginalAccountInfo(1);
        Account account = expectedAccountInfo.createAccount();
        account.setEntityId(1);
        account.setName(AccountTestData.TEST_NAME_0);
        account.setEmail("someone@somewhere.com");

        try {
            accountManager.update(account);
            Assert.fail("expected duplicate name exception for account="
                        + account);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate name, but not duplicate number or duplicate credit cards:" + ex,
                              ex.hasDuplicateAccountName() &&
                              !(ex.hasDuplicateAccountNumber() || ex.hasDuplicateCreditCardNumbers()));
            Assert.assertEquals("duplicate name", AccountTestData.TEST_NAME_0, ex.getDuplicateAccountName());
        }

        readonlyTransactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                Session session = sessionFactory.getCurrentSession();
                Account updatedAccount = (Account) session.get(Account.class, 1);

                Assert.assertEquals("account unchanged", expectedAccountInfo, new AccountInfo(updatedAccount));
                return null;
            }
        });

    }


    /**
     * Test update with duplicate credit cards. We make transactional to ensure failure does not modify the
     * DB.
     */
    @Test
    @DirtiesContext
    public void testUpdateDuplicateCreditCards() {
        final AccountInfo expectedAccountInfo = getOriginalAccountInfo(1);
        Account account = expectedAccountInfo.createAccount();
        account.setEntityId(1);
        account.addCreditCard(new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));
        account.setEmail("someone@somewhere.com");

        try {
            accountManager.update(account);
            Assert.fail("expected duplicate credit card for account="
                        + account);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate credit cards, but not duplicate number or duplicate name:" + ex,
                              ex.hasDuplicateCreditCardNumbers() &&
                              !(ex.hasDuplicateAccountNumber() || ex.hasDuplicateAccountName()));
            Assert.assertEquals("duplicate creditCards", Collections.singleton(AccountTestData.TEST_CREDIT_CARD_0),
                                ex.getDuplicateCreditCardNumbers());
        }

        readonlyTransactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                Session session = sessionFactory.getCurrentSession();
                Account updatedAccount = (Account) session.get(Account.class, 1);

                Assert.assertEquals("account unchanged", expectedAccountInfo, new AccountInfo(updatedAccount));

                return null;
            }
        });
    }


    /**
     * Test update with duplicate name and credit cards. We make transactional to ensure failure does not modify the DB.
     */
    @Test
    @DirtiesContext
    public void testUpdateDuplicateNameAndCreditCards() {
        final AccountInfo expectedAccountInfo = getOriginalAccountInfo(1);
        Account account = expectedAccountInfo.createAccount();
        account.setEntityId(1);
        account.setName(AccountTestData.TEST_NAME_0);
        account.addCreditCard(new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));

        try {
            accountManager.update(account);
            Assert.fail("expected duplicate name and credit card for account="
                        + account);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate name and credit cards, but not duplicate number:" + ex,
                              ex.hasDuplicateCreditCardNumbers() && ex.hasDuplicateAccountName() &&
                              !ex.hasDuplicateAccountNumber());
            Assert.assertEquals("duplicate name", AccountTestData.TEST_NAME_0, ex.getDuplicateAccountName());
            Assert.assertEquals("duplicate creditCards", Collections.singleton(AccountTestData.TEST_CREDIT_CARD_0),
                                ex.getDuplicateCreditCardNumbers());
        }

        readonlyTransactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                Session session = sessionFactory.getCurrentSession();
                Account updatedAccount = (Account) session.get(Account.class, 1);

                Assert.assertEquals("account unchanged", expectedAccountInfo, new AccountInfo(updatedAccount));

                return null;
            }
        });
    }


    /**
     * Test addBeneficiary
     */
    public void testAddBeneficiary() {
        accountManager.addAccountBeneficiary(0, "Georgina");

        readonlyTransactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                Session session = sessionFactory.getCurrentSession();
                Account account = (Account) session.get(Account.class, 0);

                AccountTestData.verifyAccount(account, 0,
                                              AccountTestData.TEST_NUMBER_0,
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

                return null;
            }
        });

    }

    /**
     * Test updateAccountInfo, in particular, check that beneficiaries are preserved by
     * updateAccountInfo of account attributes
     */
    @Test
    @DirtiesContext
    public void testUpdateAccountInfo() {
        Account account = AccountTestData.createTestAccount0();
        final AccountInfo accountInfo = new AccountInfo(account);
        accountInfo.setName("Ben Hale");

        accountManager.updateAccountInfo(accountInfo);

        readonlyTransactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                Session session = sessionFactory.getCurrentSession();
                Account updatedAccount = (Account) session.get(Account.class, accountInfo.getEntityId());

                AccountTestData.verifyAccount(updatedAccount, accountInfo.getEntityId(),
                                              AccountTestData.TEST_NUMBER_0,
                                              "Ben Hale",
                                              AccountTestData.TEST_DOB_0,
                                              AccountTestData.TEST_EMAIL_0,
                                              AccountTestData.TEST_RECEIVE_NEWSLETTER_0,
                                              AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_0, 2,
                                              new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));

                AccountTestData.verifyBeneficiary(updatedAccount,
                                                  AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                  AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                  AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

                AccountTestData.verifyBeneficiary(updatedAccount,
                                                  AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                                                  AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                                                  AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);

                return null;
            }
        });

    }


    /**
     * Test update for non-existent account. We make transactional to ensure failure does not modify the DB.
     */
    @Test
    @Transactional
    public void testUpdateAccountInfoNonExistentAccount() {
        Integer nonExistentId = getNonExistentId();
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo1();
        accountInfo.setEntityId(nonExistentId);
        accountInfo.setName(AccountTestData.TEST_NAME_0);

        try {
            accountManager.updateAccountInfo(accountInfo);
            Assert.fail("expected empty data result exception accountInfo=" + accountInfo);
        } catch (EmptyResultDataAccessException ex) {
            String exmsg = ex.getMessage();
            Assert.assertTrue("exception msg should include accountInfo id:" + exmsg,
                              exmsg.contains("id=" + nonExistentId));
        }
    }

    /**
     * Verify updateAccountInfo with duplicate name. We make transactional to ensure failure does not modify the DB.
     */
    @Test
    @DirtiesContext
    public void testUpdateAccountInfoDuplicateName() {
        final AccountInfo expectedAccountInfo = getOriginalAccountInfo(1);
        Account account = expectedAccountInfo.createAccount();
        account.setEntityId(1);
        AccountInfo accountInfo = new AccountInfo(account);
        accountInfo.setName(AccountTestData.TEST_NAME_0);

        try {
            accountManager.updateAccountInfo(accountInfo);
            Assert.fail("expected duplicate name for accountInfo="
                        + accountInfo);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate name, but not duplicate number or duplicate credit cards:" + ex,
                              ex.hasDuplicateAccountName() &&
                              !(ex.hasDuplicateAccountNumber() || ex.hasDuplicateCreditCardNumbers()));
            Assert.assertEquals("duplicate name", AccountTestData.TEST_NAME_0, ex.getDuplicateAccountName());
        }

        readonlyTransactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                Session session = sessionFactory.getCurrentSession();
                Account updatedAccount = (Account) session.get(Account.class, 1);

                Assert.assertEquals("account unchanged", expectedAccountInfo, new AccountInfo(updatedAccount));

                return null;
            }
        });
    }

    /**
     * Verify create from account object. We have the context re-built to clean the DB,
     * so that we use the service transaction
     */
    @Test
    @DirtiesContext
    public void testCreateAccount() {
        final Account account = AccountTestData.createTestAccount1();
        Set<CreditCard> creditCards = new HashSet<CreditCard>(account.getCreditCards());
        creditCards.add(new CreditCard(AccountTestData.TEST_CREDIT_CARD_UNUSED));
        account.setCreditCards(creditCards);

        final Set<CreditCard> expectedCreditCards = new HashSet<CreditCard>(creditCards);

        account.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_0,
                               AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_0);
        account.getBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_0)
               .credit(AccountTestData.TEST_BENEFICIARY_SAVINGS_1_0);
        account.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_1,
                               AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_1);
        account.getBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_1)
               .credit(AccountTestData.TEST_BENEFICIARY_SAVINGS_1_1);
        account.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_2,
                               AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_2);
        account.getBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_2)
               .credit(AccountTestData.TEST_BENEFICIARY_SAVINGS_1_2);

        accountManager.create(account);

        readonlyTransactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                Assert.assertNotNull("account", account);
                Session session = sessionFactory.getCurrentSession();
                session.update(account);

                AccountTestData.verifyAccount(account, -1,
                                              AccountTestData.TEST_NUMBER_1,
                                              AccountTestData.TEST_NAME_1,
                                              AccountTestData.TEST_DOB_1,
                                              AccountTestData.TEST_EMAIL_1,
                                              AccountTestData.TEST_RECEIVE_NEWSLETTER_1,
                                              AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1, 3,
                                              expectedCreditCards);

                AccountTestData.verifyBeneficiary(account,
                                                  AccountTestData.TEST_BENEFICIARY_NAME_1_0,
                                                  AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_0,
                                                  AccountTestData.TEST_BENEFICIARY_SAVINGS_1_0);

                AccountTestData.verifyBeneficiary(account,
                                                  AccountTestData.TEST_BENEFICIARY_NAME_1_1,
                                                  AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_1,
                                                  AccountTestData.TEST_BENEFICIARY_SAVINGS_1_1);

                AccountTestData.verifyBeneficiary(account,
                                                  AccountTestData.TEST_BENEFICIARY_NAME_1_2,
                                                  AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_2,
                                                  AccountTestData.TEST_BENEFICIARY_SAVINGS_1_2);

                return null;
            }
        });
    }

    /**
     * Verify create from original object with duplicate name
     */
    @Test
    @Transactional
    public void testCreateAccountDuplicateName() {
        Account account = AccountTestData.createTestAccount1();
        account.setName(AccountTestData.TEST_NAME_0);

        try {
            accountManager.create(account);
            Assert.fail("expected duplicate name for account=" + account);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate name, but not duplicate number or duplicate credit cards:" + ex,
                              ex.hasDuplicateAccountName() &&
                              !(ex.hasDuplicateAccountNumber() || ex.hasDuplicateCreditCardNumbers()));
            Assert.assertEquals("duplicate name", AccountTestData.TEST_NAME_0, ex.getDuplicateAccountName());
        }
    }

    /**
     * Verify create from original object with duplicate number
     */
    @Test
    @Transactional
    public void testCreateAccountDuplicateNumber() {
        Account account = AccountTestData.createTestAccount1();
        account.setNumber(AccountTestData.TEST_NUMBER_0);

        try {
            accountManager.create(account);
            Assert.fail("expected duplicate number for account=" + account);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate number, but not duplicate name or duplicate credit cards:" + ex,
                              ex.hasDuplicateAccountNumber() &&
                              !(ex.hasDuplicateAccountName() || ex.hasDuplicateCreditCardNumbers()));
            Assert.assertEquals("duplicate number", AccountTestData.TEST_NUMBER_0, ex.getDuplicateAccountNumber());
        }
    }

    /**
     * Verify create from original object with duplicate credit cards
     */
    @Test
    @Transactional
    public void testCreateAccountDuplicateCreditCards() {
        Account account = AccountTestData.createTestAccount1();
        account.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_0);
        account.addCreditCard(new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));

        try {
            accountManager.create(account);
            Assert.fail("expected duplicate credit cards for account=" + account);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate credit cards, but not duplicate number or duplicate name:" + ex,
                              ex.hasDuplicateCreditCardNumbers() &&
                              !(ex.hasDuplicateAccountNumber() || ex.hasDuplicateAccountName()));
            Assert.assertEquals("duplicate credit cards", Collections.singleton(AccountTestData.TEST_CREDIT_CARD_0),
                                ex.getDuplicateCreditCardNumbers());
        }
    }

    /**
     * Verify create from original object with duplicate name and number where both are in one other
     * account
     */
    @Test
    @Transactional
    public void testCreateAccountDuplicateNameAndNumber() {
        Account account = AccountTestData.createTestAccount1();
        account.setName(AccountTestData.TEST_FIRST_DUPLICATE_NAME);
        account.setNumber(AccountTestData.TEST_NUMBER_0);

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
        }
    }

    /**
     * Verify create from original object with duplicate name and credit cards where both are in one other
     * account
     */
    @Test
    @Transactional
    public void testCreateAccountDuplicateNameAndCreditCards() {
        Account account = AccountTestData.createTestAccount1();
        account.setName(AccountTestData.TEST_FIRST_DUPLICATE_NAME);
        account.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_0);
        account.addCreditCard(new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));

        try {
            accountManager.create(account);
            Assert.fail("expected duplicate name and credit cards for account="
                        + account);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate name and duplicate credit cards, but not duplicate number:" + ex,
                              ex.hasDuplicateAccountName() && ex.hasDuplicateCreditCardNumbers() &&
                              !ex.hasDuplicateAccountNumber());
            Assert.assertEquals("duplicate name", AccountTestData.TEST_FIRST_DUPLICATE_NAME,
                                ex.getDuplicateAccountName());
            Assert.assertEquals("duplicate credit cards", Collections.singleton(AccountTestData.TEST_CREDIT_CARD_0),
                                ex.getDuplicateCreditCardNumbers());
        }
    }

    /**
     * Verify create from original object with duplicate name, number and credit cards where all are in one other
     * account
     */
    @Test
    @Transactional
    public void testCreateAccountDuplicateNameNumberAndCreditCards() {
        Account account = AccountTestData.createTestAccount1();
        account.setName(AccountTestData.TEST_FIRST_DUPLICATE_NAME);
        account.setNumber(AccountTestData.TEST_NUMBER_0);
        account.addCreditCard(new CreditCard(AccountTestData.TEST_CREDIT_CARD_0));

        try {
            accountManager.create(account);
            Assert.fail("expected duplicate name, number and credit cards for account="
                        + account);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate number, name, and credit cards:" + ex,
                              ex.hasDuplicateAccountNumber() && ex.hasDuplicateAccountName() &&
                              ex.hasDuplicateCreditCardNumbers());
            Assert.assertEquals("duplicate name", AccountTestData.TEST_FIRST_DUPLICATE_NAME,
                                ex.getDuplicateAccountName());
            Assert.assertEquals("duplicate number", AccountTestData.TEST_NUMBER_0, ex.getDuplicateAccountNumber());
            Assert.assertEquals("duplicate creditCards", Collections.singleton(AccountTestData.TEST_CREDIT_CARD_0),
                                ex.getDuplicateCreditCardNumbers());
        }
    }

    /**
     * Verify create from original object with invalid beneficiary allocations
     */
    @Test
    @Transactional
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

        try {
            accountManager.create(account);
            Assert.fail("expected invalid beneficiary allocations for account="
                        + account);
        } catch (InvalidBeneficiaryAllocationsException ex) {
            final String expectedMsgContent = "total allocations gt 100%";
            Assert.assertTrue("exception message should include diagnostic '" + expectedMsgContent + "'",
                              ex.getMessage().contains(expectedMsgContent));
        }
    }

    /**
     * Verify createAccountInfo account from DTO. We have the context re-built to clean the DB,
     * so that we use the service transaction
     */
    @Test
    @DirtiesContext
    public void testCreateAccountInfo() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo1();
        Account account = AccountTestData.createTestAccount1();

        final Account createdAccount = accountManager.createAccountInfo(accountInfo);

        readonlyTransactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                Assert.assertNotNull("created account", createdAccount);
                Session session = sessionFactory.getCurrentSession();
                session.update(createdAccount);

                AccountTestData.verifyAccount(createdAccount, -1,
                                              AccountTestData.TEST_NUMBER_1,
                                              AccountTestData.TEST_NAME_1,
                                              AccountTestData.TEST_DOB_1,
                                              AccountTestData.TEST_EMAIL_1,
                                              AccountTestData.TEST_RECEIVE_NEWSLETTER_1,
                                              AccountTestData.TEST_RECEIVE_MONTHLY_EMAIL_UPDATE_1, 0,
                                              new CreditCard(AccountTestData.TEST_CREDIT_CARD_1));

                return null;
            }
        });
    }

    /**
     * Verify createAccountInfo from DTO with duplicate name
     */
    @Test
    @Transactional
    public void testCreateAccountInfoDuplicateName() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo1();
        accountInfo.setName(AccountTestData.TEST_NAME_0);

        try {
            accountManager.createAccountInfo(accountInfo);
            Assert.fail("expected duplicate name for accountInfo=" + accountInfo);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate name, but not duplicate number or duplicate credit cards:" + ex,
                              ex.hasDuplicateAccountName() &&
                              !(ex.hasDuplicateAccountNumber() || ex.hasDuplicateCreditCardNumbers()));
            Assert.assertEquals("duplicate name", AccountTestData.TEST_NAME_0, ex.getDuplicateAccountName());
        }
    }

    /**
     * Verify createAccountInfo from DTO with duplicate number
     */
    @Test
    @Transactional
    public void testCreateAccountInfoDuplicateNumber() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo1();
        accountInfo.setNumber(AccountTestData.TEST_NUMBER_0);

        try {
            accountManager.createAccountInfo(accountInfo);
            Assert.fail("expected duplicate number for accountInfo="
                        + accountInfo);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate number, but not duplicate name or duplicate credit cards:" + ex,
                              ex.hasDuplicateAccountNumber() &&
                              !(ex.hasDuplicateAccountName() || ex.hasDuplicateCreditCardNumbers()));
            Assert.assertEquals("duplicate number", AccountTestData.TEST_NUMBER_0, ex.getDuplicateAccountNumber());
        }
    }

    /**
     * Verify createAccountInfo from DTO with duplicate credit cards
     */
    @Test
    @Transactional
    public void testCreateAccountInfoDuplicateCreditCards() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo1();
        accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0));

        try {
            accountManager.createAccountInfo(accountInfo);
            Assert.fail("expected duplicate credit cardsfor accountInfo=" + accountInfo);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate credit cards, but not duplicate number or duplicate name:" + ex,
                              ex.hasDuplicateCreditCardNumbers() &&
                              !(ex.hasDuplicateAccountNumber() || ex.hasDuplicateAccountName()));
            Assert.assertEquals("duplicate credit cards", Collections.singleton(AccountTestData.TEST_CREDIT_CARD_0),
                                ex.getDuplicateCreditCardNumbers());
        }
    }

    /**
     * Verify createAccountInfo from DTO with duplicate name and number where both are in one other
     * account
     */
    @Test
    @Transactional
    public void testCreateAccountInfoDuplicateNameAndNumber() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo1();
        accountInfo.setName(AccountTestData.TEST_FIRST_DUPLICATE_NAME);
        accountInfo.setNumber(AccountTestData.TEST_NUMBER_0);

        try {
            accountManager.createAccountInfo(accountInfo);
            Assert.fail("expected duplicate name and number for accountInfo="
                        + accountInfo);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate number and duplicate name, but not duplicate credit cards:" + ex,
                              ex.hasDuplicateAccountNumber() && ex.hasDuplicateAccountName() &&
                              !ex.hasDuplicateCreditCardNumbers());
            Assert.assertEquals("duplicate name", AccountTestData.TEST_FIRST_DUPLICATE_NAME,
                                ex.getDuplicateAccountName());
            Assert.assertEquals("duplicate number", AccountTestData.TEST_NUMBER_0, ex.getDuplicateAccountNumber());
        }
    }

    /**
     * Verify createAccountInfo from DTO with duplicate name, number and credit cards where all are in one other
     * account
     */
    @Test
    @Transactional
    public void testCreateAccountInfoDuplicateNameNumberAndCreditCards() {
        AccountInfo accountInfo = AccountTestData.createTestAccountInfo1();
        accountInfo.setName(AccountTestData.TEST_FIRST_DUPLICATE_NAME);
        accountInfo.setNumber(AccountTestData.TEST_NUMBER_0);
        accountInfo.addCreditCardInfo(new CreditCardInfo(AccountTestData.TEST_CREDIT_CARD_0));

        try {
            accountManager.createAccountInfo(accountInfo);
            Assert.fail("expected duplicate name, number and credit cards for accountInfo=" + accountInfo);
        } catch (DuplicateAccountFieldsException ex) {
            Assert.assertTrue("should have duplicate number, name, and credit cards:" + ex,
                              ex.hasDuplicateAccountNumber() && ex.hasDuplicateAccountName() &&
                              ex.hasDuplicateCreditCardNumbers());
            Assert.assertEquals("duplicate name", AccountTestData.TEST_FIRST_DUPLICATE_NAME,
                                ex.getDuplicateAccountName());
            Assert.assertEquals("duplicate number", AccountTestData.TEST_NUMBER_0, ex.getDuplicateAccountNumber());
            Assert.assertEquals("duplicate creditCards", Collections.singleton(AccountTestData.TEST_CREDIT_CARD_0),
                                ex.getDuplicateCreditCardNumbers());
        }
    }

    /**
     * Test hasNoAccountWithName for name that will not be there
     */
    @Test
    public void testIsAccountNameAvailableForCreateNameUnused() {
        final String nameToTest = AccountTestData.TEST_NAME_UNUSED;

        Assert.assertTrue("should be available " + nameToTest,
                          accountManager.isAccountNameAvailableForCreate(nameToTest));
    }

    /**
     * Test hasNoAccountWithName for name that will already be there
     */
    @Test
    public void testIsAccountNameAvailableForCreateNameInUse() {
        final String nameToTest = AccountTestData.TEST_NAME_0;

        Assert.assertFalse("should not be available " + nameToTest,
                           accountManager.isAccountNameAvailableForCreate(nameToTest));
    }

    /**
     * Test isAccountNameAvailableForUpdate for name that is free
     */
    @Test
    public void testIsAccountNameAvailableForUpdateNameUnused() {
        final String nameToTest = AccountTestData.TEST_NAME_UNUSED;
        final int idToTest = 0;

        Assert.assertTrue("should be available (unused) " + nameToTest + ":id=" + idToTest,
                          accountManager.isAccountNameAvailableForUpdate(idToTest, nameToTest));
    }

    /**
     * Test isAccountNameAvailableForUpdate for name that is in use on same ID
     */
    @Test
    public void testIsAccountNameAvailableForUpdateNameInUseForSameId() {
        final String nameToTest = AccountTestData.TEST_NAME_0;
        final int idToTest = 0;

        Assert.assertTrue("should be available " + nameToTest + ":id=" + idToTest,
                          accountManager.isAccountNameAvailableForUpdate(idToTest, nameToTest));
    }

    /**
     * Test isAccountNameAvailableForUpdate for name that is in use
     */
    @Test
    public void testIsAccountNameAvailableForUpdateNameInUseForDifferentId() {
        final String nameToTest = AccountTestData.TEST_NAME_0;
        final int idToTest = 1;

        Assert.assertFalse("should not be available " + nameToTest + ":id=" + idToTest,
                           accountManager.isAccountNameAvailableForUpdate(idToTest, nameToTest));
    }

    /**
     * Test hasNoAccountWithNumber for number that will be there
     */
    @Test
    public void testIsAccountNumberAvailableForCreateNumberUnused() {
        final String numberToTest = AccountTestData.TEST_NUMBER_UNUSED;

        Assert.assertTrue("should be available " + numberToTest,
                          accountManager.isAccountNumberAvailableForCreate(numberToTest));
    }

    /**
     * Test hasNoAccountWithNumber for number that will not be there
     */
    @Test
    public void testIsAccountNumberAvailableForCreateNumberInUse() {
        final String numberToTest = AccountTestData.TEST_NUMBER_0;


        Assert.assertFalse("should not be available " + numberToTest,
                           accountManager.isAccountNumberAvailableForCreate(numberToTest));
    }

    /**
     * Test isCreditCardAvailableForCreate for card that will not be there
     */
    @Test
    public void testIsCreditCardAvailableForAccountCreateCardUnused() {
        final String cardToTest = AccountTestData.TEST_CREDIT_CARD_UNUSED;

        Assert.assertTrue("should be available " + cardToTest,
                          accountManager.isCreditCardAvailableForAccountCreate(cardToTest));
    }

    /**
     * Test isCreditCardAvailableForCreate for card in use
     */
    @Test
    public void testIsCreditCardAvailableForAccountCreateCardInUse() {
        final String cardToTest = AccountTestData.TEST_CREDIT_CARD_0;

        Assert.assertFalse("should not be available " + cardToTest,
                           accountManager.isCreditCardAvailableForAccountCreate(cardToTest));
    }

    /**
     * Test isCreditCardAvailableForUpdate for card that is in use on same ID
     */
    @Test
    public void testIsCreditCardAvailableForAccountUpdateCardUnused() {
        final String cardToTest = AccountTestData.TEST_CREDIT_CARD_UNUSED;
        final int idToTest = 0;

        Assert.assertTrue("should be available " + cardToTest + ":idToTest=" + idToTest,
                          accountManager.isCreditCardAvailableForAccountUpdate(idToTest, cardToTest));
    }

    /**
     * Test isCreditCardAvailableForUpdate for card that will not be there
     */
    @Test
    public void testIsCreditCardAvailableForAccountUpdateCardInUseSameId() {
        final String cardToTest = AccountTestData.TEST_CREDIT_CARD_0;
        final int idToTest = 0;

        Assert.assertTrue("should be available " + cardToTest + ":idToTest=" + idToTest,
                          accountManager.isCreditCardAvailableForAccountUpdate(idToTest, cardToTest));
    }

    /**
     * Test getAccountBeneficiaryInfo
     */
    @Test
    public void testGetAccountBeneficiaryInfo() {
        Account account = AccountTestData.createTestAccount0();

        final AccountBeneficiaryInfo accountBeneficiaryInfo = accountManager
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
    }

    /**
     * Test getAccountBeneficiaryInfo with non-existent ID
     */
    @Test
    public void testGetAccountBeneficiaryInfoNonExistentAccount() {
        Integer nonExistentId = getNonExistentId();
        try {
            AccountBeneficiaryInfo accountBeneficiaryInfo = accountManager.getAccountBeneficiaryInfo(nonExistentId);
            Assert.fail("expected empty data result exception accountBeneficiaryInfo=" + accountBeneficiaryInfo);
        } catch (EmptyResultDataAccessException ex) {
            String exmsg = ex.getMessage();
            Assert.assertTrue("exception msg should include account id:" + exmsg,
                              exmsg.contains("id=" + nonExistentId));
        }
    }

    /**
     * Test updateAccountBeneficiaries. We have the context re-built to clean the DB,
     * so that we use the service transaction
     */
    @Test
    @DirtiesContext
    public void testUpdateAccountBeneficiaries() {
        final Account account = AccountTestData.createTestAccount0();

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

        readonlyTransactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                Session session = sessionFactory.getCurrentSession();
                Account updatedAccount = (Account) session.get(Account.class, Integer.valueOf(0));

                AccountTestData.verifyAccount0(updatedAccount, 2);

                AccountTestData.verifyBeneficiary(updatedAccount,
                                                  AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                  AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_1,
                                                  AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

                AccountTestData.verifyBeneficiary(updatedAccount,
                                                  AccountTestData.TEST_BENEFICIARY_NAME_1_2,
                                                  AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_2,
                                                  AccountTestData.TEST_BENEFICIARY_SAVINGS_1_2);
                return null;
            }
        });
    }

    /**
     * Test updateAccountBeneficiaries with non-existent ID
     */
    @Test
    @Transactional
    public void testUpdateAccountBeneficiariesNonExistentAccount() {
        Integer nonExistentId = getNonExistentId();
        final Account account = AccountTestData.createTestAccount0();
        account.setEntityId(nonExistentId);
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

        try {
            accountManager.updateAccountBeneficiaries(accountBeneficiaryInfo);
            Assert.fail("expected empty data result exception accountBeneficiaryInfo=" + accountBeneficiaryInfo);
        } catch (EmptyResultDataAccessException ex) {
            String exmsg = ex.getMessage();
            Assert.assertTrue("exception msg should include account id:" + exmsg,
                              exmsg.contains("id=" + nonExistentId));
        }

        readonlyTransactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                Session session = sessionFactory.getCurrentSession();
                Account account0 = (Account) session.get(Account.class, 0);

                AccountTestData.verifyAccount0(account0, 2);

                AccountTestData.verifyBeneficiary(account0,
                                                  AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                  AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                  AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

                AccountTestData.verifyBeneficiary(account0,
                                                  AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                                                  AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                                                  AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);

                return null;
            }
        });
    }

    /**
     * Test updateAccountBeneficiaries with non-existent ID
     */
    @Test
    @Transactional
    public void testUpdateAccountBeneficiariesInvalidAllocations() {
        final Account account = AccountTestData.createTestAccount0();
        AccountBeneficiaryInfo accountBeneficiaryInfo = new AccountBeneficiaryInfo(
                account);

        BeneficiaryInfo beneficiaryInfo2 = new BeneficiaryInfo(
                AccountTestData.TEST_BENEFICIARY_NAME_1_2,
                AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_2,
                AccountTestData.TEST_BENEFICIARY_SAVINGS_1_2);

        accountBeneficiaryInfo.addBeneficiary(beneficiaryInfo2);

        try {
            accountManager.updateAccountBeneficiaries(accountBeneficiaryInfo);
            Assert.fail("expected exception for invalid allocations: accountBeneficiaryInfo=" +
                        accountBeneficiaryInfo);
        } catch (InvalidBeneficiaryAllocationsException ex) {

            readonlyTransactionTemplate.execute(new TransactionCallback<Object>() {
                @Override
                public Object doInTransaction(TransactionStatus transactionStatus) {
                    Session session = sessionFactory.getCurrentSession();
                    Account account0 = (Account) session.get(Account.class, 0);

                    AccountTestData.verifyAccount0(account0, 2);

                    AccountTestData.verifyBeneficiary(account0,
                                                      AccountTestData.TEST_BENEFICIARY_NAME_0_0,
                                                      AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_0,
                                                      AccountTestData.TEST_BENEFICIARY_SAVINGS_0_0);

                    AccountTestData.verifyBeneficiary(account0,
                                                      AccountTestData.TEST_BENEFICIARY_NAME_0_1,
                                                      AccountTestData.TEST_BENEFICIARY_PERCENTAGE_0_1,
                                                      AccountTestData.TEST_BENEFICIARY_SAVINGS_0_1);

                    return null;
                }
            });
        }
    }


    /**
     * Get count of accounts
     *
     * @return count
     */
    private int getAccountCount() {
        return jdbcTemplate.queryForInt("SELECT COUNT(*) FROM T_ACCOUNT");
    }


    /**
     * Get accountInfo for given ID, for comparison with subsequent updates
     *
     * @param id
     * @return account
     */
    private AccountInfo getOriginalAccountInfo(final int id) {

        return readonlyTransactionTemplate.execute(new TransactionCallback<AccountInfo>() {
            @Override
            public AccountInfo doInTransaction(TransactionStatus transactionStatus) {
                Account account = (Account) sessionFactory.getCurrentSession().get(Account.class, id);
                return new AccountInfo(account);
            }
        });

    }


    /**
     * Set values for update
     *
     * @param account
     */
    private void prepareAccountForUpdate(Account account) {
        account.setName("Ben Hale");
        account.getBeneficiary(
                AccountTestData.TEST_BENEFICIARY_NAME_0_0)
               .setAllocationPercentage(
                       AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_1);

        account.addBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_2,
                               AccountTestData.TEST_BENEFICIARY_PERCENTAGE_1_2);
        account.getBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_1_2)
               .credit(AccountTestData.TEST_BENEFICIARY_SAVINGS_1_2);
        account.removeBeneficiary(AccountTestData.TEST_BENEFICIARY_NAME_0_1);
    }


    /**
     * Get unused ID
     *
     * @return nonexistent ID
     */
    private int getNonExistentId() {
        return jdbcTemplate.queryForInt("SELECT MAX(ID)+100 FROM T_ACCOUNT");
    }
}
