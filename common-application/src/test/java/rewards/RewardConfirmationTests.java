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

package rewards;

import common.money.MonetaryAmount;
import common.money.Percentage;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for rewardConfirmation
 *
 * @author Dominic North
 */
public class RewardConfirmationTests {

    /**
     * test equals for same
     */
    @Test
    public void testEqualsEqSame() {
        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";
        final String confirmationNumber = "1";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        RewardConfirmation rewardConfirmation = new RewardConfirmation(confirmationNumber, contribution);
        Assert.assertTrue("same should be eq:rewardConfirmation=" + rewardConfirmation,
                          rewardConfirmation.equals(rewardConfirmation));
    }

    /**
     * test equals for equal values
     */
    @Test
    public void testEqualsEqEqualValues() {
        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";
        final String confirmationNumber = "1";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        AccountContribution otherContribution = new AccountContribution(accountNumber, amount,
                                                                        new HashSet<AccountContribution.Distribution>(
                                                                                distributions));

        RewardConfirmation rewardConfirmation = new RewardConfirmation(confirmationNumber, contribution);
        RewardConfirmation otherRewardConfirmation = new RewardConfirmation(confirmationNumber, otherContribution);
        Assert.assertTrue(
                "equal values should be eq:rewardConfirmation=" + rewardConfirmation + ":otherRewardConfirmation=" +
                otherRewardConfirmation,
                rewardConfirmation.equals(otherRewardConfirmation));
    }

    /**
     * test not equals null
     */
    @Test
    public void testEqualsNeNull() {
        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";
        final String confirmationNumber = "1";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        RewardConfirmation rewardConfirmation = new RewardConfirmation(confirmationNumber, contribution);
        Assert.assertFalse("null should be ne:rewardConfirmation=" + rewardConfirmation,
                          rewardConfirmation.equals(null));
    }

    /**
     * test not equals for different confirmationNumber
     */
    @Test
    public void testEqualsNeDifferentConfirmationNumber() {
        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";
        final String confirmationNumber = "1";
        final String otherConfirmationNumber = "2";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        AccountContribution otherContribution = new AccountContribution(accountNumber, amount,
                                                                        new HashSet<AccountContribution.Distribution>(
                                                                                distributions));

        RewardConfirmation rewardConfirmation = new RewardConfirmation(confirmationNumber, contribution);
        RewardConfirmation otherRewardConfirmation = new RewardConfirmation(otherConfirmationNumber,
                                                                            otherContribution);
        Assert.assertFalse(
                "different values should be ne:rewardConfirmation=" + rewardConfirmation +
                ":otherRewardConfirmation=" +
                otherRewardConfirmation,
                rewardConfirmation.equals(otherRewardConfirmation));
    }

    /**
     * test not equals for different contributions (other null)
     */
    @Test
    public void testEqualsNeOtherContributionsNull() {
        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";
        final String confirmationNumber = "1";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        AccountContribution otherContribution = new AccountContribution(accountNumber, amount, null);

        RewardConfirmation rewardConfirmation = new RewardConfirmation(confirmationNumber, contribution);
        RewardConfirmation otherRewardConfirmation = new RewardConfirmation(confirmationNumber,
                                                                            otherContribution);
        Assert.assertFalse(
                "different values should be ne:rewardConfirmation=" + rewardConfirmation +
                ":otherRewardConfirmation=" +
                otherRewardConfirmation,
                rewardConfirmation.equals(otherRewardConfirmation));
    }

    /**
     * test isEqualOrDuplicate for same
     */
    @Test
    public void testEqualOrDuplicateEqSame() {
        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";
        final String confirmationNumber = "1";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        RewardConfirmation rewardConfirmation = new RewardConfirmation(confirmationNumber, contribution);
        Assert.assertTrue("same should be eq:rewardConfirmation=" + rewardConfirmation,
                          rewardConfirmation.isEqualOrDuplicate(rewardConfirmation));
    }

    /**
     * test isEqualOrDuplicate for equal values
     */
    @Test
    public void testEqualOrDuplicateEqEqualValues() {
        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";
        final String confirmationNumber = "1";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        AccountContribution otherContribution = new AccountContribution(accountNumber, amount,
                                                                        new HashSet<AccountContribution.Distribution>(
                                                                                distributions));

        RewardConfirmation rewardConfirmation = new RewardConfirmation(confirmationNumber, contribution);
        RewardConfirmation otherRewardConfirmation = new RewardConfirmation(confirmationNumber, otherContribution);
        Assert.assertTrue(
                "equal values should be eq:rewardConfirmation=" + rewardConfirmation + ":otherRewardConfirmation=" +
                otherRewardConfirmation,
                rewardConfirmation.isEqualOrDuplicate(otherRewardConfirmation));
    }

    /**
     * test not isEqualOrDuplicate null
     */
    @Test
    public void testEqualOrDuplicateNeNull() {
        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";
        final String confirmationNumber = "1";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        RewardConfirmation rewardConfirmation = new RewardConfirmation(confirmationNumber, contribution);
        Assert.assertFalse("null should be ne:rewardConfirmation=" + rewardConfirmation,
                          rewardConfirmation.isEqualOrDuplicate(null));
    }

    /**
     * test not isEqualOrDuplicate for different confirmationNumber
     */
    @Test
    public void testEqualOrDuplicateNeDifferentConfirmationNumber() {
        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";
        final String confirmationNumber = "1";
        final String otherConfirmationNumber = "2";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        AccountContribution otherContribution = new AccountContribution(accountNumber, amount,
                                                                        new HashSet<AccountContribution.Distribution>(
                                                                                distributions));

        RewardConfirmation rewardConfirmation = new RewardConfirmation(confirmationNumber, contribution);
        RewardConfirmation otherRewardConfirmation = new RewardConfirmation(otherConfirmationNumber,
                                                                            otherContribution);
        Assert.assertFalse(
                "different values should be ne:rewardConfirmation=" + rewardConfirmation +
                ":otherRewardConfirmation=" +
                otherRewardConfirmation,
                rewardConfirmation.isEqualOrDuplicate(otherRewardConfirmation));
    }

    /**
     * test not isEqualOrDuplicate for different contributions (other different distributions)
     */
    @Test
    public void testEqualOrDuplicateNeOtherContributionsDifferent() {
        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";
        final String confirmationNumber = "1";
        final String otherBeneficiary = "ben1";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));

        Set<AccountContribution.Distribution> otherDistributions = new HashSet<AccountContribution.Distribution>();
        otherDistributions
                .add(new AccountContribution.Distribution(otherBeneficiary, amount, new Percentage(1), totalSavings));

        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        AccountContribution otherContribution = new AccountContribution(accountNumber, amount, otherDistributions);

        RewardConfirmation rewardConfirmation = new RewardConfirmation(confirmationNumber, contribution);
        RewardConfirmation otherRewardConfirmation = new RewardConfirmation(confirmationNumber,
                                                                            otherContribution);
        Assert.assertFalse(
                "different values should be ne:rewardConfirmation=" + rewardConfirmation +
                ":otherRewardConfirmation=" +
                otherRewardConfirmation,
                rewardConfirmation.isEqualOrDuplicate(otherRewardConfirmation));
    }

    /**
     * test isEqualOrDuplicate for different contributions (other null distributions)
     */
    @Test
    public void testEqualOrDuplicateEqOtherContributionsNull() {
        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";
        final String confirmationNumber = "1";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        AccountContribution otherContribution = new AccountContribution(accountNumber, amount, null);

        RewardConfirmation rewardConfirmation = new RewardConfirmation(confirmationNumber, contribution);
        RewardConfirmation otherRewardConfirmation = new RewardConfirmation(confirmationNumber,
                                                                            otherContribution);
        Assert.assertTrue(
                "different values should be eq (duplicate):rewardConfirmation=" + rewardConfirmation +
                ":otherRewardConfirmation=" +
                otherRewardConfirmation,
                rewardConfirmation.isEqualOrDuplicate(otherRewardConfirmation));
    }


    /**
     * Test Jackson Json marshalling/unmarshalling:
     * what goes in should come out!
     */
    @Test
    public void testJsonMarshallUnmarshall() throws Exception {
        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final String accountNumber = "1234567890";
        final String beneficiary0 = "ben0";
        final String beneficiary1 = "ben1";
        final MonetaryAmount totalSavings0 = new MonetaryAmount(100.0);
        final MonetaryAmount totalSavings1 = new MonetaryAmount(70.0);
        final String confirmationNumber = "1";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary0, amount, new Percentage(0.75), totalSavings0));
        distributions.add(new AccountContribution.Distribution(beneficiary1, amount, new Percentage(0.25), totalSavings1));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        RewardConfirmation rewardConfirmation = new RewardConfirmation(confirmationNumber, contribution);
        ObjectMapper mapper = new ObjectMapper();
        String marshalledVersion = mapper.writeValueAsString(rewardConfirmation);
        RewardConfirmation unmarshalledRewardConfirmation = mapper.readValue(marshalledVersion, RewardConfirmation.class);
        Assert.assertEquals("original and unmarshalled rewardConfirmation", rewardConfirmation, unmarshalledRewardConfirmation);
    }
}
