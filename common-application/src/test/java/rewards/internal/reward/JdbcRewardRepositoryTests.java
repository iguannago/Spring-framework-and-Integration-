package rewards.internal.reward;

import common.datetime.SimpleDate;
import common.money.MonetaryAmount;
import common.money.Percentage;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.util.ObjectUtils;
import rewards.AccountContribution;
import rewards.Dining;
import rewards.RewardConfirmation;
import rewards.internal.account.Account;
import rewards.internal.account.AccountTestData;
import rewards.internal.account.CreditCard;

import javax.sql.DataSource;
import java.util.*;

/**
 * Tests the JDBC reward repository with a test data source to verify data access and relational-to-object mapping
 * behavior works as expected.
 */
public class JdbcRewardRepositoryTests {

    private Logger logger = Logger.getLogger("rewards");

    private JdbcRewardRepository repository;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws Exception {
        repository = new JdbcRewardRepository();
        DataSource dataSource = createTestDataSource();
        repository.setDataSource(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    /**
     * Test simple creation of dining with default date/time
     */
    @Test
    public void testCreateRewardAccount0() {
        Dining dining = Dining.createDining("100.00", "1234123412341234", "0123456789");

        Account account = AccountTestData.createTestAccount0();

        AccountContribution contribution = account.makeContribution(new MonetaryAmount(8));
        try {
            RewardConfirmation confirmation = repository.confirmReward(contribution, dining);
            Assert.assertNotNull("confirmation should not be null", confirmation);
            Assert.assertNotNull("confirmation number should not be null", confirmation.getConfirmationNumber());
            Assert.assertEquals("wrong contribution object", contribution, confirmation.getAccountContribution());
            verifyRewardInserted(confirmation, dining);
        } catch (DataIntegrityViolationException dive) {
            logger.error("exception on creation of reward from contribution=" + contribution + ":dining=" + dining,
                         dive);
            throw dive;
        }
    }


    /**
     * Test creation of two dinings with specific date times (same date)
     */
    @Test
    public void testCreateReward2DiningsSameDateTimePlus1Hr() {
        Calendar calendar = Calendar.getInstance();
        Date date0 = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        Date date1 = calendar.getTime();
        Dining dining0 = new Dining((float) 100.0, "1234123412341234", "0123456789", date0);
        Dining dining1 = new Dining((float) 100.0, "1234123412341234", "0123456789", date1);

        Account account = AccountTestData.createTestAccount0();

        AccountContribution contribution = account.makeContribution(new MonetaryAmount(8));
        try {
            RewardConfirmation confirmation0 = repository.confirmReward(contribution, dining0);
            Assert.assertNotNull("confirmation0 should not be null", confirmation0);
            Assert.assertNotNull("confirmation0 number should not be null", confirmation0.getConfirmationNumber());
            Assert.assertEquals("wrong contribution object", contribution, confirmation0.getAccountContribution());
            verifyRewardInserted(confirmation0, dining0);

        } catch (DataIntegrityViolationException dive) {
            logger.error("exception on creation of reward from contribution=" + contribution + ":dining0=" + dining0,
                         dive);
            throw dive;
        }

        try {
            RewardConfirmation confirmation1 = repository.confirmReward(contribution, dining1);
            Assert.assertNotNull("confirmation1 should not be null", confirmation1);
            Assert.assertNotNull("confirmation1 number should not be null", confirmation1.getConfirmationNumber());
            Assert.assertEquals("wrong contribution object", contribution, confirmation1.getAccountContribution());
            verifyRewardInserted(confirmation1, dining1);

        } catch (DataIntegrityViolationException dive) {
            logger.error("exception on creation of reward from contribution=" + contribution + ":dining1=" + dining1,
                         dive);
            throw dive;
        }
    }


    /**
     * Test simple creation of dining with default date/time, no beneficiaries on the account
     */
    @Test
    public void testCreateRewardAccount8() {
        Dining dining = Dining.createDining("100.00", "4320123412340008", "123456008");

        Account account = createAccount8();

        AccountContribution contribution = account.makeContribution(new MonetaryAmount(8));
        try {
            RewardConfirmation confirmation = repository.confirmReward(contribution, dining);
            Assert.assertNotNull("confirmation should not be null", confirmation);
            Assert.assertNotNull("confirmation number should not be null", confirmation.getConfirmationNumber());
            Assert.assertEquals("wrong contribution object", contribution, confirmation.getAccountContribution());
            verifyRewardInserted(confirmation, dining);
        } catch (DataIntegrityViolationException dive) {
            logger.error("exception on creation of reward from contribution=" + contribution + ":dining=" + dining,
                         dive);
            throw dive;
        }
    }


    /**
     * Test finding reward for dining with one credit card on the account
     */
    @Test
    public void testFindRewardForDiningAccount0() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        Dining dining = new Dining((float) 100.0, "1234123412341234", "0123456789", date);
        Account account = AccountTestData.createTestAccount0();
        AccountContribution contribution = account.makeContribution(new MonetaryAmount(8));
        RewardConfirmation confirmation0 = repository.confirmReward(contribution, dining);
        AccountContribution expectedContribution =
                new AccountContribution(contribution.getAccountNumber(), contribution.getAmount(),
                                        contribution.getDistributions());
        RewardConfirmation expectedConfirmation =
                new RewardConfirmation(confirmation0.getConfirmationNumber(), expectedContribution);

        RewardConfirmation confirmation = repository.findConfirmationFor(dining);
        Assert.assertEquals("confirmation found", expectedConfirmation, confirmation);
    }


    /**
     * Test finding reward for dining with one credit card on the account
     */
    @Test
    public void testFindRewardForDiningAccount4Card0() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        Dining dining = new Dining((float) 100.0, "1234123412340004", "0123456789", date);
        Account account = createAccount4();

        AccountContribution contribution = account.makeContribution(new MonetaryAmount(8));
        RewardConfirmation confirmation4 = repository.confirmReward(contribution, dining);
        AccountContribution expectedContribution =
                new AccountContribution(contribution.getAccountNumber(), contribution.getAmount(),
                                        contribution.getDistributions());
        RewardConfirmation expectedConfirmation =
                new RewardConfirmation(confirmation4.getConfirmationNumber(), expectedContribution);

        RewardConfirmation confirmation = repository.findConfirmationFor(dining);
        Assert.assertEquals("confirmation found", expectedConfirmation, confirmation);
    }


    /**
     * Test finding reward for dining with one credit card on the account
     */
    @Test
    public void testFindRewardForDiningAccount4Card1() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        Dining dining = new Dining((float) 100.0, "4320123412340005", "0123456789", date);
        Account account = createAccount4();

        AccountContribution contribution = account.makeContribution(new MonetaryAmount(8));
        RewardConfirmation confirmation4 = repository.confirmReward(contribution, dining);
        AccountContribution expectedContribution =
                new AccountContribution(contribution.getAccountNumber(), contribution.getAmount(),
                                        contribution.getDistributions());
        RewardConfirmation expectedConfirmation =
                new RewardConfirmation(confirmation4.getConfirmationNumber(), expectedContribution);

        RewardConfirmation confirmation = repository.findConfirmationFor(dining);
        Assert.assertEquals("confirmation found", expectedConfirmation, confirmation);
    }


    /**
     * Test finding reward for dining with one credit card on the account, no beneficiaries
     */
    @Test
    public void testFindRewardForDiningAccount8() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        Dining dining = new Dining((float) 100.0, "4320123412340008", "123456008", date);
        Account account = createAccount8();
        AccountContribution contribution = account.makeContribution(new MonetaryAmount(8));
        RewardConfirmation confirmation0 = repository.confirmReward(contribution, dining);
        AccountContribution expectedContribution =
                new AccountContribution(contribution.getAccountNumber(), contribution.getAmount(),
                                        contribution.getDistributions());
        RewardConfirmation expectedConfirmation =
                new RewardConfirmation(confirmation0.getConfirmationNumber(), expectedContribution);

        RewardConfirmation confirmation = repository.findConfirmationFor(dining);
        Assert.assertEquals("confirmation found", expectedConfirmation, confirmation);
    }


    /**
     * Test (not) finding reward for dining with one credit card on the account, different dining time
     */
    @Test
    public void testFindRewardForDiningNoDining() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        Dining dining = new Dining((float) 100.0, "1234123412341234", "0123456789", date);

        RewardConfirmation confirmation = repository.findConfirmationFor(dining);
        Assert.assertNull("confirmation should not found for dining=" + dining + ":confirmation=" + confirmation,
                          confirmation);
    }


    /**
     * Test (not) finding reward for dining with one credit card on the account, different dining time
     */
    @Test
    public void testFindRewardForDiningAccount0DifferentTime() {
        Date now = new Date();
        Dining diningNow = new Dining(100.00f, "1234123412341234", "0123456789", now);
        Date older = new Date(now.getTime() - 60000);
        Dining diningOlder = new Dining(100.00f, "1234123412341234", "0123456789", older);

        Account account = AccountTestData.createTestAccount0();
        AccountContribution contribution = account.makeContribution(new MonetaryAmount(8));
        RewardConfirmation confirmation0 = repository.confirmReward(contribution, diningOlder);

        RewardConfirmation confirmation = repository.findConfirmationFor(diningNow);
        Assert.assertNull("confirmation should not found for dining=" + diningNow + ":confirmation=" + confirmation,
                          confirmation);
    }


    /**
     * Helper to check what should have been inserted DINING_MERCHANT_NUMBER, DINING_DATE, DINING_AMOUNT
     *
     * @param confirmation
     * @param dining
     */
    private void verifyRewardInserted(RewardConfirmation confirmation, Dining dining) {
        Assert.assertEquals(1, getRewardCount(confirmation.getConfirmationNumber()));

        Map<String, Map<String, Object>> expectedRows = new TreeMap<String, Map<String, Object>>();
        AccountContribution contribution = confirmation.getAccountContribution();

        final String beneficiaryName = "BENEFICIARY_NAME";

        final Set<AccountContribution.Distribution> contributionDistributions = contribution.getDistributions();
        if (contributionDistributions.size() > 0) {
            for (AccountContribution.Distribution distribution : contributionDistributions) {
                Map<String, Object> expectedRow = new LinkedHashMap<String, Object>();

                expectedRow.put("CONFIRMATION_NUMBER", confirmation.getConfirmationNumber());
                expectedRow.put("ACCOUNT_NUMBER", contribution.getAccountNumber());
                expectedRow.put("REWARD_AMOUNT", contribution.getAmount().asDouble());
                expectedRow.put("DINING_MERCHANT_NUMBER", dining.getMerchantNumber());
                expectedRow.put("DINING_DATE", dining.getDate());
                expectedRow.put("DINING_AMOUNT", (double) dining.getAmount());
                expectedRow.put(beneficiaryName, distribution.getBeneficiary());
                expectedRow.put("DISTRIBUTION_AMOUNT", distribution.getAmount().asDouble());
                expectedRow.put("ALLOCATION_PERCENTAGE", distribution.getPercentage().asDouble());
                expectedRow.put("BENEFICIARY_SAVINGS", distribution.getTotalSavings().asDouble());

                expectedRows.put(distribution.getBeneficiary(), expectedRow);
            }

        } else {
            Map<String, Object> expectedRow = new LinkedHashMap<String, Object>();

            expectedRow.put("CONFIRMATION_NUMBER", confirmation.getConfirmationNumber());
            expectedRow.put("ACCOUNT_NUMBER", contribution.getAccountNumber());
            expectedRow.put("REWARD_AMOUNT", contribution.getAmount().asDouble());
            expectedRow.put("DINING_MERCHANT_NUMBER", dining.getMerchantNumber());
            expectedRow.put("DINING_DATE", dining.getDate());
            expectedRow.put("DINING_AMOUNT", (double) dining.getAmount());
            expectedRow.put(beneficiaryName, null);
            expectedRow.put("DISTRIBUTION_AMOUNT", null);
            expectedRow.put("ALLOCATION_PERCENTAGE", null);
            expectedRow.put("BENEFICIARY_SAVINGS", null);

            expectedRows.put("__NULL__", expectedRow);
        }

        List<Map<String, Object>> actualRows = jdbcTemplate.queryForList(
                "select r.CONFIRMATION_NUMBER, r.ACCOUNT_NUMBER, r.REWARD_AMOUNT, r.DINING_MERCHANT_NUMBER, " +
                "r.DINING_DATE, r.DINING_AMOUNT, d.BENEFICIARY_NAME, d.DISTRIBUTION_AMOUNT, d.ALLOCATION_PERCENTAGE, " +
                "d.BENEFICIARY_SAVINGS from T_REWARD r left outer join T_REWARD_DISTRIBUTION d on r.ID = d.REWARD_ID " +
                "where CONFIRMATION_NUMBER = ?",
                confirmation.getConfirmationNumber());

        boolean rowsEqual = true;
        StringBuffer neMsg = new StringBuffer();
        for (Map<String, Object> actualRow : actualRows) {

            final Object actualBeneficiaryName = actualRow.get(beneficiaryName);
            Map<String, Object> expectedRow = expectedRows.get(actualBeneficiaryName == null ? "__NULL__" : actualBeneficiaryName);
            if (!expectedRow.equals(actualRow)) {
                if (rowsEqual) {
                    rowsEqual = false;
                    neMsg.append("expectedRow != actualRow ['").append(beneficiaryName).append("']:");
                }

                for (String columnName : expectedRow.keySet()) {
                    final Object expectedValue = expectedRow.get(columnName);
                    final Object actualValue = actualRow.get(columnName);
                    if (!ObjectUtils.nullSafeEquals(expectedValue, actualValue)) {
                        neMsg.append("\n  ['").append(columnName).append("']:expectedValue=");
                        if (expectedValue != null) {
                            neMsg.append('(').append(expectedValue.getClass()).append(")");
                        }
                        neMsg.append(expectedValue).append(":actualValue=");
                        if (actualValue != null) {
                            neMsg.append('(').append(actualValue.getClass()).append(")");
                        }
                        neMsg.append(actualValue);
                    }
                }

                Assert.assertTrue(neMsg.toString(), rowsEqual);
            }
        }

        Assert.assertEquals("rows.size():", expectedRows.size(), actualRows.size());
    }


    /**
     * Create account 4 (multiple credit cards)
     *
     * @return account
     */
    private Account createAccount4() {
        Account account = new Account("123456004", "Chad I. Cobbs",
                                      new SimpleDate(4, 23, 1976).asDate(), "chadc@netzero.com", true, false,
                                      new CreditCard("1234123412340004"), new CreditCard("4320123412340005"));

        final Percentage percent50 = new Percentage(0.5);
        final Percentage percent25 = new Percentage(0.25);
        account.addBeneficiary("Jane", percent25);
        account.addBeneficiary("Amy", percent25);
        account.addBeneficiary("Susan", percent50);
        return account;
    }


    /**
     * Create account 8 (0 beneficiaries)
     *
     * @return account
     */
    private Account createAccount8() {
        Account account = new Account("123456008", "Jean Sans Enfant",
                                      new SimpleDate(4, 23, 1976).asDate(), "jse@qui.fr", true, false, new CreditCard("4320123412340008"));

        return account;
    }


    /**
     * Count reweard count for confirmation number
     *
     * @param confirmationNumber
     * @return
     */
    private int getRewardCount(String confirmationNumber) {
        return jdbcTemplate
                .queryForInt("select count(*) from T_REWARD where CONFIRMATION_NUMBER = ?", confirmationNumber);
    }


    /**
     * Create test dataSource
     *
     * @return
     */
    private DataSource createTestDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setName("rewards")
                .addScript("/rewards/testdb/schema.sql")
                .addScript("/rewards/testdb/test-data.sql")
                .addScript("/rewards/testdb/test-data-additional-cards.sql")
                .build();
    }
}
