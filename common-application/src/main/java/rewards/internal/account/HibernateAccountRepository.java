package rewards.internal.account;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Finds account objects using the Hibernate API.
 */
@Repository
public class HibernateAccountRepository implements AccountRepository {

    /**
     * Session factory
     */
    private SessionFactory sessionFactory;

    /**
     * Logger
     */
    private Logger logger = Logger.getLogger("rewards");

    /**
     * Creates an new hibernate-based account repository.
     *
     * @param sessionFactory the Hibernate session factory required to obtain sessions
     */
    public HibernateAccountRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Get all accounts
     *
     * @return list of accounts
     * @see rewards.internal.account.AccountRepository#getAllAccounts()
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Account> getAllAccounts() {
        return (List<Account>) getCurrentSession().createQuery("from Account").list();
    }

    /**
     * Get account by ID, or null if it doesn't exist
     *
     * @param id
     * @return account
     * @see rewards.internal.account.AccountRepository#getAccount(Integer)
     */
    @Override
    public Account getAccount(Integer id) {
        return (Account) getCurrentSession().get(Account.class, id);
    }

    /**
     * Check there is not already an account with the supplied name, so that it
     * can be used for creation
     *
     * @param accountName
     * @return true if it is available, false if it is already in use
     * @see rewards.internal.account.AccountRepository#hasNoAccountWithName(String)
     */
    @Override
    public boolean hasNoAccountWithName(final String accountName) {
        Query query = getCurrentSession().createQuery(
                "select count(a) from Account a where a.name = :name");
        query = query.setString("name", accountName);
        Long accountCt = (Long) query.uniqueResult();
        return (accountCt == 0);
    }

    /**
     * Check there is not already an account with the supplied number, so that
     * it can be used for creation
     *
     * @param accountNumber
     * @return true if it is available, false if it is already in use
     * @see rewards.internal.account.AccountRepository#hasNoAccountWithNumber(String)
     */
    @Override
    public boolean hasNoAccountWithNumber(
            final String accountNumber) {
        Query query = getCurrentSession().createQuery(
                "select count(a) from Account a where a.number = :number");
        query.setString("number", accountNumber);
        Long accountCt = (Long) query.uniqueResult();
        return (accountCt == 0);
    }

    /**
     * Check there is no account using the supplied credit card numbers.
     *
     * @param creditCardNumber
     * @return true if it is available, false if it is already in use
     * @see rewards.internal.account.AccountRepository#hasNoAccountWithCreditCard(String)
     */
    @Override
    public boolean hasNoAccountWithCreditCard(final String creditCardNumber) {
        Query query = getCurrentSession().createQuery(
                "select count(a) from Account a join a.creditCards c where c.creditCardNumber = :creditCardNumber");
        query = query.setString("creditCardNumber", creditCardNumber);
        Long accountCt = (Long) query.uniqueResult();
        return (accountCt == 0);
    }

    /**
     * Load an account by its account number.
     *
     * @param accountNumber the account number
     * @return the account object
     */
    @Override
    public Account findByAccountNumber(String accountNumber) {
        Query query = getCurrentSession().createQuery(
                "from Account a where a.number = :accountNumber");
        query.setString("accountNumber", accountNumber);
        return (Account) query.uniqueResult();
    }

    /**
     * Load an account by its credit card number.
     *
     * @param creditCardNumber the credit card number
     * @return the account object
     * @see rewards.internal.account.AccountRepository#findByCreditCard(String)
     */
    @Override
    public Account findByCreditCard(String creditCardNumber) {
        Query query = getCurrentSession().createQuery(
                "select a from Account a join a.creditCards c where c.creditCardNumber = :creditCardNumber");
        query.setString("creditCardNumber", creditCardNumber);
        return (Account) query.uniqueResult();
    }

    /**
     * Persist updated account attributes
     *
     * @param account object with new values, including appropriate entityId
     * @see rewards.internal.account.AccountRepository#update(Account)
     */
    @Override
    public void update(Account account) {
        // Nothing to do in Hibernate
        logger.debug("updated account=" + account);
    }

    /**
     * Persist new account
     *
     * @param account object with new values
     * @see rewards.internal.account.AccountRepository#create(Account)
     */
    @Override
    public void create(Account account) {
        getCurrentSession().save(account);
    }

    /**
     * Returns the session associated with the ongoing reward transaction.
     *
     * @return the transactional session
     */
    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}