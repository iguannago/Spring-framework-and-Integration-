package rewards.internal.reward;

import common.money.MonetaryAmount;
import common.money.Percentage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import rewards.AccountContribution;
import rewards.RewardConfirmation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Unit test class for {@link RewardConfirmationResultSetExtractor}
 * <p/>
 * User: djnorth
 * Date: 13/07/2012
 * Time: 14:59
 */
public class RewardConfirmationResultSetExtractorTests {

    RewardConfirmationResultSetExtractor extractor = new RewardConfirmationResultSetExtractor();
    ResultSet resultSet;

    /**
     * Setup result set
     */
    @Before
    public void setupResultSet() {
        resultSet = Mockito.mock(ResultSet.class);
    }


    /**
     * Test {@link RewardConfirmationResultSetExtractor#extractData(java.sql.ResultSet)} for 0 rewards
     */
    @Test
    public void testExtractData0Rewards() throws Exception {

        final String[] confirmationNumbers = {};
        final String[] accountNumbers = {};
        final double[] amounts = {};

        final AccountContribution.Distribution[][] distributions = {};

        Mockito.when(resultSet.next()).thenReturn(false);

        verifyExtractData(confirmationNumbers, accountNumbers, amounts, distributions);
    }


    /**
     * Test {@link RewardConfirmationResultSetExtractor#extractData(java.sql.ResultSet)} for 1 reward with distributions
     */
    @Test
    public void testExtractData1RewardWithDistributions() throws Exception {

        final String[] confirmationNumbers = {"1"};
        final String[] accountNumbers = {"123456789"};
        final double[] amounts = {8.0};

        final AccountContribution.Distribution[][] distributions = {
                {
                        new AccountContribution.Distribution("Annabelle", new MonetaryAmount(6.0), new Percentage(0.75),
                                                             new MonetaryAmount(254.0)),
                        new AccountContribution.Distribution("Corgan", new MonetaryAmount(2.0), new Percentage(0.25),
                                                             new MonetaryAmount(129.0))
                }
        };

        Mockito.when(resultSet.next()).thenReturn(true, true, false);

        Mockito.when(resultSet.getString("CONFIRMATION_NUMBER"))
               .thenReturn(confirmationNumbers[0], confirmationNumbers[0]);
        Mockito.when(resultSet.getString("ACCOUNT_NUMBER")).thenReturn(accountNumbers[0]);
        Mockito.when(resultSet.getDouble("REWARD_AMOUNT")).thenReturn(amounts[0]);

        Mockito.when(resultSet.getString("BENEFICIARY_NAME"))
               .thenReturn(distributions[0][0].getBeneficiary(), distributions[0][1].getBeneficiary());
        Mockito.when(resultSet.getDouble("DISTRIBUTION_AMOUNT"))
               .thenReturn(distributions[0][0].getAmount().asDouble(), distributions[0][1].getAmount().asDouble());
        Mockito.when(resultSet.getDouble("ALLOCATION_PERCENTAGE"))
               .thenReturn(distributions[0][0].getPercentage().asDouble(),
                           distributions[0][1].getPercentage().asDouble());
        Mockito.when(resultSet.getDouble("BENEFICIARY_SAVINGS"))
               .thenReturn(distributions[0][0].getTotalSavings().asDouble(),
                           distributions[0][1].getTotalSavings().asDouble());

        verifyExtractData(confirmationNumbers, accountNumbers, amounts, distributions);
    }


    /**
     * Test {@link RewardConfirmationResultSetExtractor#extractData(java.sql.ResultSet)} for 1 reward with no
     * distribution
     */
    @Test
    public void testExtractData1RewardNoDistributions() throws Exception {

        final String[] confirmationNumbers = {"1"};
        final String[] accountNumbers = {"123456789"};
        final double[] amounts = {8.0};

        final AccountContribution.Distribution[][] distributions = {{}};

        Mockito.when(resultSet.next()).thenReturn(true, true, false);

        Mockito.when(resultSet.getString("CONFIRMATION_NUMBER"))
               .thenReturn(confirmationNumbers[0], confirmationNumbers[0]);
        Mockito.when(resultSet.getString("ACCOUNT_NUMBER")).thenReturn(accountNumbers[0]);
        Mockito.when(resultSet.getDouble("REWARD_AMOUNT")).thenReturn(amounts[0]);

        Mockito.when(resultSet.getString("BENEFICIARY_NAME")).thenReturn(null);

        verifyExtractData(confirmationNumbers, accountNumbers, amounts, distributions);
    }


    /**
     * Test {@link RewardConfirmationResultSetExtractor#extractData(java.sql.ResultSet)} for 2 rewards with
     * distributions
     */
    @Test
    public void testExtractData2RewardsWithDistributions() throws Exception {

        final String[] confirmationNumbers = {"1", "3"};
        final String[] accountNumbers = {"123456789", "123400001"};
        final double[] amounts = {8.0, 10.0};

        final AccountContribution.Distribution[][] distributions = {
                {
                        new AccountContribution.Distribution("Annabelle", new MonetaryAmount(6.0), new Percentage(0.75),
                                                             new MonetaryAmount(254.0)),
                        new AccountContribution.Distribution("Corgan", new MonetaryAmount(2.0), new Percentage(0.25),
                                                             new MonetaryAmount(129.0))
                },
                {
                        new AccountContribution.Distribution("Arthur", new MonetaryAmount(10.0), new Percentage(1.0),
                                                             new MonetaryAmount(1254.5)),
                }

        };

        Mockito.when(resultSet.next()).thenReturn(true, true, true, false);

        Mockito.when(resultSet.getString("CONFIRMATION_NUMBER"))
               .thenReturn(confirmationNumbers[0], confirmationNumbers[0], confirmationNumbers[1]);
        Mockito.when(resultSet.getString("ACCOUNT_NUMBER")).thenReturn(accountNumbers[0], accountNumbers[1]);
        Mockito.when(resultSet.getDouble("REWARD_AMOUNT")).thenReturn(amounts[0], amounts[1]);

        Mockito.when(resultSet.getString("BENEFICIARY_NAME"))
               .thenReturn(distributions[0][0].getBeneficiary(), distributions[0][1].getBeneficiary(),
                           distributions[1][0].getBeneficiary());
        Mockito.when(resultSet.getDouble("DISTRIBUTION_AMOUNT"))
               .thenReturn(distributions[0][0].getAmount().asDouble(), distributions[0][1].getAmount().asDouble(),
                           distributions[1][0].getAmount().asDouble());
        Mockito.when(resultSet.getDouble("ALLOCATION_PERCENTAGE"))
               .thenReturn(distributions[0][0].getPercentage().asDouble(),
                           distributions[0][1].getPercentage().asDouble(),
                           distributions[1][0].getPercentage().asDouble());
        Mockito.when(resultSet.getDouble("BENEFICIARY_SAVINGS"))
               .thenReturn(distributions[0][0].getTotalSavings().asDouble(),
                           distributions[0][1].getTotalSavings().asDouble(),
                           distributions[1][0].getTotalSavings().asDouble());

        verifyExtractData(confirmationNumbers, accountNumbers, amounts, distributions);
    }


    /**
     * Test {@link RewardConfirmationResultSetExtractor#extractData(java.sql.ResultSet)} for 2 rewards, 1 with
     * distributions, 1 without
     */
    @Test
    public void testExtractData2Rewards1WithDistributions1NoDistributions() throws Exception {

        final String[] confirmationNumbers = {"1", "3"};
        final String[] accountNumbers = {"123456789", "123400001"};
        final double[] amounts = {8.0, 10.0};

        final AccountContribution.Distribution[][] distributions = {
                {},
                {
                        new AccountContribution.Distribution("Arthur", new MonetaryAmount(10.0), new Percentage(1.0),
                                                             new MonetaryAmount(1254.5)),
                }

        };

        Mockito.when(resultSet.next()).thenReturn(true, true, false);

        Mockito.when(resultSet.getString("CONFIRMATION_NUMBER"))
               .thenReturn(confirmationNumbers[0], confirmationNumbers[1]);
        Mockito.when(resultSet.getString("ACCOUNT_NUMBER")).thenReturn(accountNumbers[0], accountNumbers[1]);
        Mockito.when(resultSet.getDouble("REWARD_AMOUNT")).thenReturn(amounts[0], amounts[1]);

        Mockito.when(resultSet.getString("BENEFICIARY_NAME")).thenReturn(null, distributions[1][0].getBeneficiary());
        Mockito.when(resultSet.getDouble("DISTRIBUTION_AMOUNT")).thenReturn(distributions[1][0].getAmount().asDouble());
        Mockito.when(resultSet.getDouble("ALLOCATION_PERCENTAGE")).thenReturn(distributions[1][0].getPercentage().asDouble());
        Mockito.when(resultSet.getDouble("BENEFICIARY_SAVINGS")).thenReturn(distributions[1][0].getTotalSavings().asDouble());

        verifyExtractData(confirmationNumbers, accountNumbers, amounts, distributions);
    }


    /**
     * Test {@link RewardConfirmationResultSetExtractor#extractData(java.sql.ResultSet)} for 3 rewards with
     * distributions
     */
    @Test
    public void testExtractData3RewardsWithDistributions() throws Exception {

        final String[] confirmationNumbers = {"1", "3", "77"};
        final String[] accountNumbers = {"123456789", "123400001", "777888900"};
        final double[] amounts = {8.0, 10.0, 50.0};

        final AccountContribution.Distribution[][] distributions = {
                {
                        new AccountContribution.Distribution("Annabelle", new MonetaryAmount(6.0), new Percentage(0.75),
                                                             new MonetaryAmount(254.0)),
                        new AccountContribution.Distribution("Corgan", new MonetaryAmount(2.0), new Percentage(0.25),
                                                             new MonetaryAmount(129.0))
                },
                {
                        new AccountContribution.Distribution("Arthur", new MonetaryAmount(10.0), new Percentage(1.0),
                                                             new MonetaryAmount(1254.5))
                },
                {
                        new AccountContribution.Distribution("Rodney", new MonetaryAmount(20.0), new Percentage(0.4),
                                                             new MonetaryAmount(506.33)),
                        new AccountContribution.Distribution("Zafira", new MonetaryAmount(15.0), new Percentage(0.3),
                                                             new MonetaryAmount(229.0)),
                        new AccountContribution.Distribution("Zachery", new MonetaryAmount(15.0), new Percentage(0.3),
                                                             new MonetaryAmount(129.0))
                }

        };

        Mockito.when(resultSet.next()).thenReturn(true, true, true, true, true, true, false);

        Mockito.when(resultSet.getString("CONFIRMATION_NUMBER"))
               .thenReturn(confirmationNumbers[0], confirmationNumbers[0], confirmationNumbers[1],
                           confirmationNumbers[2], confirmationNumbers[2], confirmationNumbers[2]);
        Mockito.when(resultSet.getString("ACCOUNT_NUMBER"))
               .thenReturn(accountNumbers[0], accountNumbers[1], accountNumbers[2]);
        Mockito.when(resultSet.getDouble("REWARD_AMOUNT")).thenReturn(amounts[0], amounts[1], amounts[2]);

        Mockito.when(resultSet.getString("BENEFICIARY_NAME"))
               .thenReturn(distributions[0][0].getBeneficiary(), distributions[0][1].getBeneficiary(),
                           distributions[1][0].getBeneficiary(), distributions[2][0].getBeneficiary(),
                           distributions[2][1].getBeneficiary(), distributions[2][2].getBeneficiary());
        Mockito.when(resultSet.getDouble("DISTRIBUTION_AMOUNT"))
               .thenReturn(distributions[0][0].getAmount().asDouble(), distributions[0][1].getAmount().asDouble(),
                           distributions[1][0].getAmount().asDouble(), distributions[2][0].getAmount().asDouble(),
                           distributions[2][1].getAmount().asDouble(), distributions[2][2].getAmount().asDouble());
        Mockito.when(resultSet.getDouble("ALLOCATION_PERCENTAGE"))
               .thenReturn(distributions[0][0].getPercentage().asDouble(),
                           distributions[0][1].getPercentage().asDouble(),
                           distributions[1][0].getPercentage().asDouble(),
                           distributions[2][0].getPercentage().asDouble(),
                           distributions[2][1].getPercentage().asDouble(),
                           distributions[2][2].getPercentage().asDouble());
        Mockito.when(resultSet.getDouble("BENEFICIARY_SAVINGS"))
               .thenReturn(distributions[0][0].getTotalSavings().asDouble(),
                           distributions[0][1].getTotalSavings().asDouble(),
                           distributions[1][0].getTotalSavings().asDouble(),
                           distributions[2][0].getTotalSavings().asDouble(),
                           distributions[2][1].getTotalSavings().asDouble(),
                           distributions[2][2].getTotalSavings().asDouble());

        verifyExtractData(confirmationNumbers, accountNumbers, amounts, distributions);
    }


    /**
     * Test {@link RewardConfirmationResultSetExtractor#extractData(java.sql.ResultSet)} for 3 rewards with
     * distributions
     */
    @Test
    public void testExtractData3Rewards2WithDistributions1NoDistributions() throws Exception {

        final String[] confirmationNumbers = {"1", "3", "77"};
        final String[] accountNumbers = {"123456789", "123400001", "777888900"};
        final double[] amounts = {8.0, 10.0, 50.0};

        final AccountContribution.Distribution[][] distributions = {
                {
                        new AccountContribution.Distribution("Annabelle", new MonetaryAmount(6.0), new Percentage(0.75),
                                                             new MonetaryAmount(254.0)),
                        new AccountContribution.Distribution("Corgan", new MonetaryAmount(2.0), new Percentage(0.25),
                                                             new MonetaryAmount(129.0))
                },
                {},
                {
                        new AccountContribution.Distribution("Rodney", new MonetaryAmount(20.0), new Percentage(0.4),
                                                             new MonetaryAmount(506.33)),
                        new AccountContribution.Distribution("Zafira", new MonetaryAmount(15.0), new Percentage(0.3),
                                                             new MonetaryAmount(229.0)),
                        new AccountContribution.Distribution("Zachery", new MonetaryAmount(15.0), new Percentage(0.3),
                                                             new MonetaryAmount(129.0))
                }

        };

        Mockito.when(resultSet.next()).thenReturn(true, true, true, true, true, true, false);

        Mockito.when(resultSet.getString("CONFIRMATION_NUMBER"))
               .thenReturn(confirmationNumbers[0], confirmationNumbers[0], confirmationNumbers[1],
                           confirmationNumbers[2], confirmationNumbers[2], confirmationNumbers[2]);
        Mockito.when(resultSet.getString("ACCOUNT_NUMBER"))
               .thenReturn(accountNumbers[0], accountNumbers[1], accountNumbers[2]);
        Mockito.when(resultSet.getDouble("REWARD_AMOUNT")).thenReturn(amounts[0], amounts[1], amounts[2]);

        Mockito.when(resultSet.getString("BENEFICIARY_NAME"))
               .thenReturn(distributions[0][0].getBeneficiary(), distributions[0][1].getBeneficiary(),
                           null, distributions[2][0].getBeneficiary(),
                           distributions[2][1].getBeneficiary(), distributions[2][2].getBeneficiary());
        Mockito.when(resultSet.getDouble("DISTRIBUTION_AMOUNT"))
               .thenReturn(distributions[0][0].getAmount().asDouble(), distributions[0][1].getAmount().asDouble(),
                           distributions[2][0].getAmount().asDouble(), distributions[2][1].getAmount().asDouble(), distributions[2][2].getAmount().asDouble());
        Mockito.when(resultSet.getDouble("ALLOCATION_PERCENTAGE"))
               .thenReturn(distributions[0][0].getPercentage().asDouble(),
                           distributions[0][1].getPercentage().asDouble(),
                           distributions[2][0].getPercentage().asDouble(),
                           distributions[2][1].getPercentage().asDouble(),
                           distributions[2][2].getPercentage().asDouble());
        Mockito.when(resultSet.getDouble("BENEFICIARY_SAVINGS"))
               .thenReturn(distributions[0][0].getTotalSavings().asDouble(),
                           distributions[0][1].getTotalSavings().asDouble(),
                           distributions[2][0].getTotalSavings().asDouble(),
                           distributions[2][1].getTotalSavings().asDouble(),
                           distributions[2][2].getTotalSavings().asDouble());

        verifyExtractData(confirmationNumbers, accountNumbers, amounts, distributions);
    }


    /**
     * Helper to verify returned reward confirmations
     *
     * @param confirmationNumbers
     * @param accountNumbers
     * @param amounts
     * @param distributions
     * @throws SQLException
     */
    private void verifyExtractData(String[] confirmationNumbers, String[] accountNumbers, double[] amounts,
                                   AccountContribution.Distribution[][] distributions) throws SQLException {

        if (confirmationNumbers.length != accountNumbers.length || accountNumbers.length != amounts.length ||
            amounts.length != distributions.length) {
            throw new IllegalArgumentException("inconsistent lengths for arguments");
        }

        List<RewardConfirmation> actualConfirmations = extractor.extractData(resultSet);

        Assert.assertEquals("confirmations.size()", confirmationNumbers.length, actualConfirmations.size());

        for (int i = 0; i < confirmationNumbers.length; ++i) {
            RewardConfirmation actualRewardConfirmation = actualConfirmations.get(i);
            Assert.assertEquals("confirmationNumbers", confirmationNumbers[i],
                                actualRewardConfirmation.getConfirmationNumber());

            AccountContribution actualAccountContribution = actualRewardConfirmation.getAccountContribution();
            Assert.assertEquals("contribution.accountNumbers", accountNumbers[i],
                                actualAccountContribution.getAccountNumber());
            Assert.assertEquals("contribution.amounts", new MonetaryAmount(amounts[i]),
                                actualAccountContribution.getAmount());

            Assert.assertEquals("contribution.distributions.size()", distributions[i].length,
                                actualAccountContribution.getDistributions().size());

            for (int j = 0; distributions[i] != null && j < distributions[i].length; ++j) {
                Assert.assertEquals("distribution[" + j + "]", distributions[i][j],
                                    actualAccountContribution.getDistribution(distributions[i][j].getBeneficiary()));
            }
        }
    }
}
