package rewards;

import common.money.MonetaryAmount;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;


/**
 * A system test that verifies the components of the RewardNetwork application work together to reward for dining
 * successfully. Uses Spring to bootstrap the application for use in a test environment.
 */
@ContextConfiguration("classpath:/rewards/common-application-test-config.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class RewardNetworkTests {

    /**
     * The object being tested.
     */
    @Autowired
    private RewardNetwork rewardNetwork;


    /**
     * Test with valid dining
     */
    @Test
    public void testRewardForDining() {
        // createAccountInfo a new dining of 100.00 charged to credit card '1234123412341234' by merchant '123457890'
        // as test input
        Dining dining = Dining.createDining("100.00", "1234123412341234", "1234567890");

        // call the 'rewardNetwork' to test its rewardAccountFor(Dining) method
        RewardConfirmation confirmation = rewardNetwork.rewardAccountFor(dining);

        // assert the expected reward confirmation results
        Assert.assertNotNull(confirmation);
        Assert.assertNotNull(confirmation.getConfirmationNumber());

        // assert an account contribution was made
        AccountContribution contribution = confirmation.getAccountContribution();
        Assert.assertNotNull(contribution);

        // the contribution account number should be '123456789'
        Assert.assertEquals("123456789", contribution.getAccountNumber());

        // the total contribution amount should be 8.00 (8% of 100.00)
        final MonetaryAmount amount8 = new MonetaryAmount(8);
        Assert.assertEquals("amount", new MonetaryAmount(8), contribution.getAmount());

        // each distribution should be 4.00 (as both have a 50% allocation)
        final MonetaryAmount amount4 = new MonetaryAmount(4);
        Assert.assertEquals(amount4, contribution.getDistribution("Annabelle").getAmount());
        Assert.assertEquals(amount4, contribution.getDistribution("Corgan").getAmount());
    }


    /**
     * Test with valid dining no beneficiaries
     */
    @Test
    public void testRewardForDiningNoBeneficiaries() {
        // createAccountInfo a new dining of 100.00 charged to credit card '1234123412340005' by merchant '123457890'
        // as test input
        Dining dining = Dining.createDining("100.00", "1234123412340005", "1234567890");

        // call the 'rewardNetwork' to test its rewardAccountFor(Dining) method
        RewardConfirmation confirmation = rewardNetwork.rewardAccountFor(dining);

        // assert the expected reward confirmation results
        Assert.assertNotNull(confirmation);
        Assert.assertNotNull(confirmation.getConfirmationNumber());

        // assert an account contribution was made
        AccountContribution contribution = confirmation.getAccountContribution();
        Assert.assertNotNull(contribution);

        // the contribution account number should be '123456789'
        Assert.assertEquals("123456005", contribution.getAccountNumber());

        // the total contribution amount should be 8.00 (8% of 100.00)
        final MonetaryAmount amount8 = new MonetaryAmount(8);
        Assert.assertEquals("amount", new MonetaryAmount(8), contribution.getAmount());

        // each distribution should be 4.00 (as both have a 50% allocation)
        final MonetaryAmount amount4 = new MonetaryAmount(4);
        Assert.assertEquals(0, contribution.getDistributions().size());
    }


    /**
     * Test with dining, bad creditCardNumber
     */
    @Test
    public void testRewardForDiningBadCreditCard() {
        // createAccountInfo a new dining of 100.00 charged to credit card '1234123412341235' by merchant '123457890'
        // as test input
        Dining dining = Dining.createDining("100.00", "1234123412341235", "1234567890");

        // call the 'rewardNetwork' to test its rewardAccountFor(Dining) method
        RewardConfirmation confirmation = null;
        try {
            confirmation = rewardNetwork.rewardAccountFor(dining);
            Assert.fail("expected EmptyResultDataAccessException for account");
        } catch (EmptyResultDataAccessException edae) {
            Assert.assertEquals("expected count should be 1 in exception:" + edae, 1, edae.getExpectedSize());
            Assert.assertEquals("actual count should be 0 in exception:" + edae, 0, edae.getActualSize());
            Assert.assertTrue("'account' in message:" + edae.getMessage(),
                              edae.getMessage().contains("account "));
            Assert.assertTrue("merchantNumber in message:" + edae.getMessage(),
                              edae.getMessage().contains(dining.getMerchantNumber()));
        }
    }


    /**
     * Test with dining, bad merchantNumber
     */
    @Test
    public void testRewardForDiningBadMerchantNumber() {
        // createAccountInfo a new dining of 100.00 charged to credit card '1234123412341235' by merchant '123457890'
        // as test input
        Dining dining = Dining.createDining("100.00", "1234123412341234", "1234567891");

        // call the 'rewardNetwork' to test its rewardAccountFor(Dining) method
        RewardConfirmation confirmation = null;
        try {
            confirmation = rewardNetwork.rewardAccountFor(dining);
            Assert.fail("expected EmptyResultDataAccessException for restaurant");
        } catch (EmptyResultDataAccessException edae) {
            Assert.assertEquals("expected count should be 1 in exception:" + edae, 1, edae.getExpectedSize());
            Assert.assertEquals("actual count should be 0 in exception:" + edae, 0, edae.getActualSize());
            Assert.assertTrue("'restaurant' in message:" + edae.getMessage(),
                              edae.getMessage().contains("restaurant "));
            Assert.assertTrue("merchantNumber in message:" + edae.getMessage(),
                              edae.getMessage().contains(dining.getMerchantNumber()));
        }
    }


    /**
     * Test with valid dining, but duplicate values
     */
    @Test(expected = DuplicateKeyException.class)
    public void testRewardForDiningDuplicateDining() {
        // createAccountInfo a new dining of 100.00 charged to credit card '1234123412341234' by merchant '123457890'
        // as test input
        Dining dining = new Dining(100.0f, "1234123412341234", "1234567890", new Date());
        Dining duplicateDining =
                new Dining(dining.getAmount(), dining.getCreditCardNumber(), dining.getMerchantNumber(),
                           dining.getDate());

        // call the 'rewardNetwork' to test its rewardAccountFor(Dining) method
        RewardConfirmation confirmation = rewardNetwork.rewardAccountFor(dining);

        // assert the expected reward confirmation results
        Assert.assertNotNull(confirmation);
        Assert.assertNotNull(confirmation.getConfirmationNumber());

        // assert an account contribution was made
        AccountContribution contribution = confirmation.getAccountContribution();
        Assert.assertNotNull(contribution);

        // the contribution account number should be '123456789'
        Assert.assertEquals("123456789", contribution.getAccountNumber());

        // the total contribution amount should be 8.00 (8% of 100.00)
        final MonetaryAmount amount8 = new MonetaryAmount(8);
        Assert.assertEquals("amount", new MonetaryAmount(8), contribution.getAmount());

        // each distribution should be 4.00 (as both have a 50% allocation)
        final MonetaryAmount amount4 = new MonetaryAmount(4);
        Assert.assertEquals(amount4, contribution.getDistribution("Annabelle").getAmount());
        Assert.assertEquals(amount4, contribution.getDistribution("Corgan").getAmount());

        RewardConfirmation duplicateConfirmation = rewardNetwork.rewardAccountFor(duplicateDining);
    }


    /**
     * Test findRewardFor reward that exists
     */
    @Test
    public void testFindRewardForDining() {
        // createAccountInfo a new dining of 100.00 charged to credit card '1234123412341234' by merchant '123457890'
        // as test input
        Dining dining = Dining.createDining("200.00", "1234123412341234", "1234567890");

        // call the 'rewardNetwork' to test its rewardAccountFor(Dining) method to create a confirmation
        RewardConfirmation expectedConfirmation = rewardNetwork.rewardAccountFor(dining);

        RewardConfirmation actualConfirmation = rewardNetwork.findConfirmationFor(dining);

        // assert the expected reward confirmation results
        Assert.assertNotNull("rewardConfirmation found", actualConfirmation);
        Assert.assertEquals("rewardConfirmation found", expectedConfirmation, actualConfirmation);
    }


    /**
     * Test findRewardFor reward that does not exist
     */
    @Test
    public void testFindRewardForDiningNonExistent() {
        // createAccountInfo a new dining of 100.00 charged to credit card '1234123412341234' by merchant '123457890'
        // as test input
        Dining dining = Dining.createDining("300.00", "1234123412341234", "1234567890");

        try {
            RewardConfirmation actualConfirmation = rewardNetwork.findConfirmationFor(dining);
            Assert.fail("expected exception for non-existent confirmation, but got return:" + actualConfirmation);
        } catch (EmptyResultDataAccessException e) {
            // As expected
        }
    }
}
