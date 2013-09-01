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
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for AccountContribution
 *
 * @author Dominic North
 */
public class AccountContributionTests {

    /**
     * Test equals for same object
     */
    @Test
    public void testEqualsEqSame() {

        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        Assert.assertEquals("same contribution should be equal", contribution, contribution);
    }

    /**
     * Test equals for equivalent object
     */
    @Test
    public void testEqualsEqEqualValues() {

        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        AccountContribution otherContribution = new AccountContribution(accountNumber, amount,
                                                                        new HashSet<AccountContribution.Distribution>(
                                                                                distributions));

        Assert.assertEquals("same values in contribution should be equal", contribution, otherContribution);
    }

    /**
     * Test not equals for null object
     */
    @Test
    public void testEqualsNeNull() {

        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        Assert.assertFalse("null should not be equal", contribution.equals(null));
    }

    /**
     * Test not equals for different accountNumber
     */
    @Test
    public void testEqualsNeDifferentAccountNumber() {

        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String otherAccountNumber = "1234567891";
        final String beneficiary = "ben0";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        AccountContribution otherContribution = new AccountContribution(otherAccountNumber, amount,
                                                                        new HashSet<AccountContribution.Distribution>(
                                                                                distributions));

        Assert.assertFalse("different values in contribution should not be equal:contribution=" + contribution +
                           ":otherContribution=" + otherContribution, contribution.equals(otherContribution));
    }

    /**
     * Test not equals for different amount
     */
    @Test
    public void testEqualsNeDifferentAmount() {

        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount otherAmount = new MonetaryAmount(11.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        AccountContribution otherContribution = new AccountContribution(accountNumber, otherAmount,
                                                                        new HashSet<AccountContribution.Distribution>(
                                                                                distributions));

        Assert.assertFalse("different values in contribution should not be equal:contribution=" + contribution +
                           ":otherContribution=" + otherContribution, contribution.equals(otherContribution));
    }

    /**
     * Test not equals for different distributions
     */
    @Test
    public void testEqualsNeDifferentDistributions() {

        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";
        final String otherBeneficiary = "ben1";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));

        Set<AccountContribution.Distribution> otherDistributions = new HashSet<AccountContribution.Distribution>();
        otherDistributions
                .add(new AccountContribution.Distribution(otherBeneficiary, amount, new Percentage(1), totalSavings));

        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        AccountContribution otherContribution = new AccountContribution(accountNumber, amount, otherDistributions);

        Assert.assertFalse("different values in contribution should not be equal:contribution=" + contribution +
                           ":otherContribution=" + otherContribution, contribution.equals(otherContribution));
    }

    /**
     * Test not equals for different distributions
     */
    @Test
    public void testEqualsNeThisNullDistributions() {

        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";
        final String otherBeneficiary = "ben1";

        Set<AccountContribution.Distribution> distributions = null;

        Set<AccountContribution.Distribution> otherDistributions = new HashSet<AccountContribution.Distribution>();
        otherDistributions
                .add(new AccountContribution.Distribution(otherBeneficiary, amount, new Percentage(1), totalSavings));

        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        AccountContribution otherContribution = new AccountContribution(accountNumber, amount, otherDistributions);

        Assert.assertFalse("different values in contribution should not be equal:contribution=" + contribution +
                           ":otherContribution=" + otherContribution, contribution.equals(otherContribution));
    }

    /**
     * Test not equals for different distributions
     */
    @Test
    public void testEqualsNeOtherNullDistributions() {

        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";
        final String otherBeneficiary = "ben1";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions
                .add(new AccountContribution.Distribution(otherBeneficiary, amount, new Percentage(1), totalSavings));

        Set<AccountContribution.Distribution> otherDistributions = null;

        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        AccountContribution otherContribution = new AccountContribution(accountNumber, amount, otherDistributions);

        Assert.assertFalse("different values in contribution should not be equal:contribution=" + contribution +
                           ":otherContribution=" + otherContribution, contribution.equals(otherContribution));
    }

    /**
     * Test equals for same object
     */
    @Test
    public void testEqualOrDuplicateEqSame() {

        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        Assert.assertTrue("same contribution should be equal:contribution=" + contribution,
                          contribution.isEqualOrDuplicate(contribution));
    }

    /**
     * Test equals for exactly equivalent object
     */
    @Test
    public void testEqualOrDuplicateEqEqualValues() {

        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        AccountContribution otherContribution = new AccountContribution(accountNumber, amount,
                                                                        new HashSet<AccountContribution.Distribution>(
                                                                                distributions));

        Assert.assertTrue("same values in contribution should be equal:contribution=" + contribution +
                          ":otherContribution=" + otherContribution,
                          contribution.isEqualOrDuplicate(otherContribution));
    }

    /**
     * Test not equals for null object
     */
    @Test
    public void testEqualOrDuplicateNeNull() {

        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        Assert.assertFalse("null should not be equal:contribution=" + contribution, contribution.isEqualOrDuplicate(null));
    }

    /**
     * Test not isEqualOrDuplicate for different accountNumber
     */
    @Test
    public void testEqualOrDuplicateNeDifferentAccountNumber() {

        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String otherAccountNumber = "1234567891";
        final String beneficiary = "ben0";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        AccountContribution otherContribution = new AccountContribution(otherAccountNumber, amount,
                                                                        new HashSet<AccountContribution.Distribution>(
                                                                                distributions));

        Assert.assertFalse("different values in contribution should not be equal:contribution=" + contribution +
                           ":otherContribution=" + otherContribution, contribution.isEqualOrDuplicate(otherContribution));
    }

    /**
     * Test not isEqualOrDuplicate for different amount
     */
    @Test
    public void testEqualOrDuplicateNeDifferentAmount() {

        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount otherAmount = new MonetaryAmount(11.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));
        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        AccountContribution otherContribution = new AccountContribution(accountNumber, otherAmount,
                                                                        new HashSet<AccountContribution.Distribution>(
                                                                                distributions));

        Assert.assertFalse("different values in contribution should not be equal:contribution=" + contribution +
                           ":otherContribution=" + otherContribution, contribution.isEqualOrDuplicate(otherContribution));
    }

    /**
     * Test not isEqualOrDuplicate for different distributions
     */
    @Test
    public void testEqualOrDuplicateNeDifferentDistributions() {

        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";
        final String otherBeneficiary = "ben1";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(new AccountContribution.Distribution(beneficiary, amount, new Percentage(1), totalSavings));

        Set<AccountContribution.Distribution> otherDistributions = new HashSet<AccountContribution.Distribution>();
        otherDistributions
                .add(new AccountContribution.Distribution(otherBeneficiary, amount, new Percentage(1), totalSavings));

        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        AccountContribution otherContribution = new AccountContribution(accountNumber, amount, otherDistributions);

        Assert.assertFalse("different values in contribution should not be equal:contribution=" + contribution +
                           ":otherContribution=" + otherContribution, contribution.isEqualOrDuplicate(otherContribution));
    }

    /**
     * Test isEqualOrDuplicate for different distributions (this null)
     */
    @Test
    public void testEqualOrDuplicateNeThisNullDistributions() {

        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";
        final String otherBeneficiary = "ben1";

        Set<AccountContribution.Distribution> distributions = null;

        Set<AccountContribution.Distribution> otherDistributions = new HashSet<AccountContribution.Distribution>();
        otherDistributions
                .add(new AccountContribution.Distribution(otherBeneficiary, amount, new Percentage(1), totalSavings));

        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        AccountContribution otherContribution = new AccountContribution(accountNumber, amount, otherDistributions);

        Assert.assertTrue("same attribute, this null distributions:contribution=" + contribution +
                           ":otherContribution=" + otherContribution, contribution.isEqualOrDuplicate(otherContribution));
    }

    /**
     * Test isEqualOrDuplicate for different distributions (other null)
     */
    @Test
    public void testEqualOrDuplicateNeOtherNullDistributions() {

        final MonetaryAmount amount = new MonetaryAmount(10.0);
        final MonetaryAmount totalSavings = new MonetaryAmount(100.0);
        final String accountNumber = "1234567890";
        final String beneficiary = "ben0";
        final String otherBeneficiary = "ben1";

        Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions
                .add(new AccountContribution.Distribution(otherBeneficiary, amount, new Percentage(1), totalSavings));

        Set<AccountContribution.Distribution> otherDistributions = null;

        AccountContribution contribution = new AccountContribution(accountNumber, amount, distributions);

        AccountContribution otherContribution = new AccountContribution(accountNumber, amount, otherDistributions);

        Assert.assertTrue("same attributes, other null distributions:contribution=" + contribution +
                           ":otherContribution=" + otherContribution, contribution.isEqualOrDuplicate(otherContribution));
    }
}
