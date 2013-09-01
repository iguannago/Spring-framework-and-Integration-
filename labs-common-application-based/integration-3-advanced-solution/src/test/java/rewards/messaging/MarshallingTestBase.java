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

package rewards.messaging;

import common.money.MonetaryAmount;
import common.money.Percentage;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.xml.xpath.Jaxp13XPathTemplate;
import org.springframework.xml.xpath.XPathOperations;
import rewards.AccountContribution;
import rewards.RewardConfirmation;

import javax.xml.transform.Source;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dominic North
 */
public abstract class MarshallingTestBase {
    /**
     * Template method to perform the marshalling we want to test
     *
     * @param confirmation
     * @return source
     */
    protected abstract Source doMarshallRewardConfirmation(RewardConfirmation confirmation) throws Exception;

    /**
     * Test reward with distributions
     *
     * @throws Exception
     */
    @Test
    public void outboundConfirmationWithDistributions() throws Exception {
        final AccountContribution.Distribution distribution0 =
                new AccountContribution.Distribution("Ben0", new MonetaryAmount(7.5), new Percentage(0.75),
                                                     new MonetaryAmount(250));
        final AccountContribution.Distribution distribution1 =
                new AccountContribution.Distribution("Ben1", new MonetaryAmount(2.5), new Percentage(0.25),
                                                     new MonetaryAmount(100));

        final Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();
        distributions.add(distribution0);
        distributions.add(distribution1);

        final AccountContribution contribution =
                new AccountContribution("123456789", new MonetaryAmount(10), distributions);

        final RewardConfirmation confirmation = new RewardConfirmation("1", contribution);

        Source xmlConfirmation = doMarshallRewardConfirmation(confirmation);

        final XPathOperations xpathTemplate = new Jaxp13XPathTemplate();

        Assert.assertEquals("confirmationNumber:" + xmlConfirmation, confirmation.getConfirmationNumber(),
                            xpathTemplate.evaluateAsString(
                                    "/reward-confirmation/@confirmation-number", xmlConfirmation));
        Assert.assertEquals("contribution.accountNumber:" + xmlConfirmation, contribution.getAccountNumber(),
                            xpathTemplate.evaluateAsString(
                                    "/reward-confirmation/account-contribution/@account-number", xmlConfirmation));
        final Double amount = xpathTemplate.evaluateAsDouble(
                "/reward-confirmation/account-contribution/@amount", xmlConfirmation);
        Assert.assertEquals("contribution.amount:" + xmlConfirmation, contribution.getAmount(),
                            new MonetaryAmount(amount));

        Assert.assertEquals("distribution0.beneficiary:" + xmlConfirmation, distribution0.getBeneficiary(),
                            xpathTemplate.evaluateAsString(
                                    "/reward-confirmation/account-contribution/distributions/distribution[1" +
                                    "]/@beneficiary",
                                    xmlConfirmation));

        final Double amount0 = xpathTemplate.evaluateAsDouble(
                "/reward-confirmation/account-contribution/distributions/distribution[1]/@amount",
                xmlConfirmation);
        Assert.assertEquals("distribution0.amount:" + xmlConfirmation, distribution0.getAmount(),
                            new MonetaryAmount(amount0));

        final Double percentage0 = xpathTemplate.evaluateAsDouble(
                "/reward-confirmation/account-contribution/distributions/distribution[1]/@percentage",
                xmlConfirmation);
        Assert.assertEquals("distribution0.percentage:" + xmlConfirmation, distribution0.getPercentage(),
                            new Percentage(percentage0));

        final Double savings0 = xpathTemplate.evaluateAsDouble(
                "/reward-confirmation/account-contribution/distributions/distribution[1]/@total-savings",
                xmlConfirmation);
        Assert.assertEquals("distribution0.savings:" + xmlConfirmation, distribution0.getTotalSavings(),
                            new MonetaryAmount(savings0));

        Assert.assertEquals("distribution1.beneficiary:" + xmlConfirmation, distribution1.getBeneficiary(),
                            xpathTemplate.evaluateAsString(
                                    "/reward-confirmation/account-contribution/distributions/distribution[2" +
                                    "]/@beneficiary",
                                    xmlConfirmation));

        final Double amount1 = xpathTemplate.evaluateAsDouble(
                "/reward-confirmation/account-contribution/distributions/distribution[2]/@amount",
                xmlConfirmation);
        Assert.assertEquals("distribution1.amount:" + xmlConfirmation, distribution1.getAmount(),
                            new MonetaryAmount(amount1));

        final Double percentage1 = xpathTemplate.evaluateAsDouble(
                "/reward-confirmation/account-contribution/distributions/distribution[2]/@percentage",
                xmlConfirmation);
        Assert.assertEquals("distribution1.percentage:" + xmlConfirmation, distribution1.getPercentage(),
                            new Percentage(percentage1));

        final Double savings1 = xpathTemplate.evaluateAsDouble(
                "/reward-confirmation/account-contribution/distributions/distribution[2]/@total-savings",
                xmlConfirmation);
        Assert.assertEquals("distribution1.savings:" + xmlConfirmation, distribution1.getTotalSavings(),
                            new MonetaryAmount(savings1));
    }

    /**
     * Test reward with no distributions
     *
     * @throws Exception
     */
    @Test
    public void outboundConfirmationWithoutDistributions() throws Exception {

        final Set<AccountContribution.Distribution> distributions = new HashSet<AccountContribution.Distribution>();

        final AccountContribution contribution =
                new AccountContribution("123456789", new MonetaryAmount(10), distributions);

        final RewardConfirmation confirmation = new RewardConfirmation("1", contribution);

        Source xmlConfirmation = doMarshallRewardConfirmation(confirmation);

        final XPathOperations xpathTemplate = new Jaxp13XPathTemplate();

        Assert.assertEquals("confirmationNumber:" + xmlConfirmation, confirmation.getConfirmationNumber(),
                            xpathTemplate.evaluateAsString(
                                    "/reward-confirmation/@confirmation-number", xmlConfirmation));
        Assert.assertEquals("contribution.accountNumber:" + xmlConfirmation, contribution.getAccountNumber(),
                            xpathTemplate.evaluateAsString(
                                    "/reward-confirmation/account-contribution/@account-number", xmlConfirmation));
        final Double amount = xpathTemplate.evaluateAsDouble(
                "/reward-confirmation/account-contribution/@amount", xmlConfirmation);
        Assert.assertEquals("contribution.amount:" + xmlConfirmation, contribution.getAmount(),
                            new MonetaryAmount(amount));

    }
}
