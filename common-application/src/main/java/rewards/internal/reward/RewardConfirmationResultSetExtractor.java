/*
 *
 *  * Copyright 2002-2011 the original author or authors, or Red-Black IT Ltd, as appropriate.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package rewards.internal.reward;

import common.money.MonetaryAmount;
import common.money.Percentage;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import rewards.AccountContribution;
import rewards.RewardConfirmation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Result extractor for rewards
 *
 * @author Dominic North
 */
public class RewardConfirmationResultSetExtractor implements ResultSetExtractor<List<RewardConfirmation>> {

    /**
     * Create reward confirmations and there distributions from entire resultset, so that we can re-constitute the
     * distributions in the AccountContribution, so this has become a resultset extractor.
     *
     * @param resultSet
     * @return reward confirmation list (never null)
     * @throws SQLException
     */
    @Override
    public List<RewardConfirmation> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        List<RewardConfirmation> rewardConfirmations = new ArrayList<RewardConfirmation>();
        String confirmationNumber = null;
        String accountNumber = null;
        MonetaryAmount amount = null;
        Set<AccountContribution.Distribution> distributions = null;

        while (resultSet.next()) {

            final String thisConfirmationNumber = resultSet.getString("CONFIRMATION_NUMBER");

            if (!thisConfirmationNumber.equals(confirmationNumber)) {
                buildLastRewardConfirmation(rewardConfirmations, confirmationNumber, accountNumber, amount,
                                            distributions);
                distributions = new HashSet<AccountContribution.Distribution>();
                confirmationNumber = thisConfirmationNumber;
                accountNumber = resultSet.getString("ACCOUNT_NUMBER");
                amount = new MonetaryAmount(resultSet.getDouble("REWARD_AMOUNT"));
            }

            final String beneficiaryName = resultSet.getString("BENEFICIARY_NAME");
            if (beneficiaryName != null) {
                final MonetaryAmount distributionAmount =
                        new MonetaryAmount(resultSet.getDouble("DISTRIBUTION_AMOUNT"));
                final Percentage allocationPercentage = new Percentage(resultSet.getDouble("ALLOCATION_PERCENTAGE"));
                final MonetaryAmount beneficiarySavings =
                        new MonetaryAmount(resultSet.getDouble("BENEFICIARY_SAVINGS"));
                AccountContribution.Distribution distribution =
                        new AccountContribution.Distribution(beneficiaryName, distributionAmount, allocationPercentage,
                                                             beneficiarySavings);
                distributions.add(distribution);
            }
        }

        buildLastRewardConfirmation(rewardConfirmations, confirmationNumber, accountNumber, amount, distributions);

        return rewardConfirmations;
    }


    /**
     * Helper that will build a reward confirmation for the provided confirmation number,
     * assuming that there is one (confirmationNumber not null)
     *
     * @param rewardConfirmations
     * @param confirmationNumber
     * @param accountNumber
     * @param amount
     * @param distributions
     */
    private void buildLastRewardConfirmation(List<RewardConfirmation> rewardConfirmations, String confirmationNumber,
                                             String accountNumber, MonetaryAmount amount,
                                             Set<AccountContribution.Distribution> distributions) {
        if (confirmationNumber != null) {
            final AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);
            final RewardConfirmation rewardConfirmation = new RewardConfirmation(confirmationNumber, contribution);
            rewardConfirmations.add(rewardConfirmation);
        }
    }
}
