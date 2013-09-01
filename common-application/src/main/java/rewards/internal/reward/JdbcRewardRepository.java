package rewards.internal.reward;

import common.datetime.SimpleDate;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import rewards.AccountContribution;
import rewards.Dining;
import rewards.RewardConfirmation;

import javax.sql.DataSource;
import java.util.List;

/**
 * JDBC implementation of a reward repository that records the result of a reward transaction by inserting a reward
 * confirmation record.
 */
@Repository
public class JdbcRewardRepository implements RewardRepository {

    /**
     * DataSource
     */
    private JdbcTemplate jdbcTemplate;

    /**
     * ResulSet Extractor
     */
    private final RewardConfirmationResultSetExtractor
            rewardConfirmationResultSetExtractor = new RewardConfirmationResultSetExtractor();

    /**
     * Setter for injecting dataSource
     *
     * @param dataSource
     */
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    /**
     * Create a record of a reward that will track a contribution made to an account for dining.
     *
     * @param contribution the account contribution that was made
     * @param dining       the dining event that resulted in the account contribution
     * @return reward confirmation
     */
    public RewardConfirmation confirmReward(AccountContribution contribution, Dining dining) {

        final String rewardSql =
                "insert into T_REWARD (CONFIRMATION_NUMBER, REWARD_AMOUNT, REWARD_DATE, ACCOUNT_NUMBER, " +
                "DINING_MERCHANT_NUMBER, DINING_DATE, DINING_AMOUNT) values (?, ?, ?, ?, ?, ?, ?)";
        final String confirmationNumber = nextConfirmationNumber();
        final String distributionSql =
                "insert into T_REWARD_DISTRIBUTION (REWARD_ID, BENEFICIARY_NAME, DISTRIBUTION_AMOUNT, " +
                "ALLOCATION_PERCENTAGE, BENEFICIARY_SAVINGS) values (?, ?, ?, ?, ?)";

        jdbcTemplate.update(rewardSql, confirmationNumber, contribution.getAmount().asBigDecimal(),
                            SimpleDate.today().asDate(), contribution.getAccountNumber(), dining.getMerchantNumber(),
                            dining.getDate(), dining.getAmount());

        final long rewardId =
                jdbcTemplate.queryForLong("select ID from T_REWARD where CONFIRMATION_NUMBER = ?", confirmationNumber);

        for (AccountContribution.Distribution distribution : contribution.getDistributions()) {
            jdbcTemplate.update(distributionSql, rewardId, distribution.getBeneficiary(),
                                distribution.getAmount().asBigDecimal(), distribution.getPercentage().asBigDecimal(),
                                distribution.getTotalSavings().asBigDecimal());
        }

        final RewardConfirmation rewardConfirmation = new RewardConfirmation(confirmationNumber, contribution);
        return rewardConfirmation;
    }

    /**
     * Finds a RewardConfirmation related to this Dining.
     *
     * @param dining
     * @return the RewardConfirmation for this particular Dining, <code>null</code> otherwise
     */
    @Override
    public RewardConfirmation findConfirmationFor(Dining dining) {
        final String sql =
                "select r.CONFIRMATION_NUMBER, r.REWARD_AMOUNT, r.ACCOUNT_NUMBER, r.DINING_DATE, d.BENEFICIARY_NAME, " +
                "d.DISTRIBUTION_AMOUNT, d.ALLOCATION_PERCENTAGE, d.BENEFICIARY_SAVINGS from T_REWARD r left outer " +
                "join T_REWARD_DISTRIBUTION d on r.ID = d.REWARD_ID, " +
                "T_ACCOUNT a, T_ACCOUNT_CREDIT_CARD cc where a.NUMBER = r.ACCOUNT_NUMBER and a.ID = cc.ACCOUNT_ID and" +
                " r.DINING_AMOUNT = ? and cc.NUMBER = ? and r.DINING_MERCHANT_NUMBER = ? and r" +
                ".DINING_DATE = ?";

        List<RewardConfirmation> rewardConfirmations = jdbcTemplate.query(sql, rewardConfirmationResultSetExtractor,
                                                                          dining.getAmount(),
                                                                          dining.getCreditCardNumber(),
                                                                          dining.getMerchantNumber(), dining.getDate());

        if (rewardConfirmations.size() > 1) {
            throw new IncorrectResultSizeDataAccessException("multiple confirmations for dining=" + dining, 1);
        }

        final RewardConfirmation rewardConfirmation =
                (rewardConfirmations.isEmpty() ? null : rewardConfirmations.get(0));
        return rewardConfirmation;
    }

    /**
     * Helper to get next confirmation number
     *
     * @return next number
     */
    private String nextConfirmationNumber() {
        String sql = "select next value for S_REWARD_CONFIRMATION_NUMBER from DUAL_REWARD_CONFIRMATION_NUMBER";
        return jdbcTemplate.queryForObject(sql, String.class);
    }


}