package rewards.internal;

import common.money.MonetaryAmount;
import common.money.Percentage;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import rewards.AccountContribution;
import rewards.Dining;
import rewards.RewardConfirmation;
import rewards.internal.account.Account;
import rewards.internal.account.AccountRepository;
import rewards.internal.account.AccountTestData;
import rewards.internal.restaurant.BenefitAvailabilityPolicy;
import rewards.internal.restaurant.Restaurant;
import rewards.internal.restaurant.RestaurantRepository;
import rewards.internal.reward.RewardRepository;

import java.util.Random;

/**
 * Unit tests for the RewardNetworkImpl application logic. Configures the
 * implementation with stub repositories containing dummy data for fast
 * in-memory testing without the overhead of an external data source.
 * <p/>
 * Besides helping catch bugs early, tests are a great way for a new developer
 * to learn an API as he or she can see the API in action. Tests also help
 * validate a design as they are a measure for how easy it is to use your code.
 */
public class RewardNetworkImplMockTests {

    /**
     * The object being tested.
     */
    private RewardNetworkImpl rewardNetwork;

    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger("rewards");

    /**
     * Mocks used
     */
    private AccountRepository         accountRepo;
    private RestaurantRepository      restaurantRepo;
    private RewardRepository          rewardRepo;
    private BenefitAvailabilityPolicy benefitAvailabilityPolicy;

    /**
     * Prepare mocks
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        // createAccountInfo stubs to facilitate fast in-memory testing with dummy data and
        // no external dependencies
        accountRepo = EasyMock.createMock(AccountRepository.class);
        restaurantRepo = EasyMock.createMock(RestaurantRepository.class);
        rewardRepo = EasyMock.createMock(RewardRepository.class);
        benefitAvailabilityPolicy = BenefitAvailabilityPolicy.ALWAYS_AVAILABLE;

        // setup the object being tested by handing what it needs to work
        rewardNetwork = new RewardNetworkImpl(accountRepo, restaurantRepo,
                                              rewardRepo);
    }


    /**
     * Usual test values, but with mocks
     */
    @Test
    public void testRewardForDining() {
        // createAccountInfo a new dining of 100.00 charged to credit card
        // '1234123412341234' by merchant '123457890' as test input
        Dining dining = Dining.createDining("100.00", "1234123412341234",
                                            "1234567890");

        Account account = AccountTestData.createTestAccount0();
        final MonetaryAmount expectedAmount = new MonetaryAmount(8);
        final AccountContribution expectedContribution = account.makeContribution(expectedAmount);

        // Reset account, as makeContribution updates beneficiaries
        account = AccountTestData.createTestAccount0();

        EasyMock.expect(
                accountRepo.findByCreditCard(dining.getCreditCardNumber()))
                .andReturn(account);

        Restaurant restaurant = new Restaurant("1234567890", "Apple Bees");
        restaurant.setBenefitPercentage(new Percentage(0.08));
        restaurant.setBenefitAvailabilityPolicy(benefitAvailabilityPolicy);
        EasyMock.expect(
                restaurantRepo.findByMerchantNumber(dining.getMerchantNumber()))
                .andReturn(restaurant);

        String confirmationNumber = new Random().toString();
        RewardConfirmation expectedConfirmation = new RewardConfirmation(
                confirmationNumber, expectedContribution);

        EasyMock.expect(rewardRepo.confirmReward(expectedContribution, dining)).andReturn(expectedConfirmation);
        EasyMock.replay(accountRepo, restaurantRepo, rewardRepo);

        // call the 'rewardNetwork' to test its rewardAccountFor(Dining) method
        RewardConfirmation confirmation = rewardNetwork
                .rewardAccountFor(dining);

        // assert the expected reward confirmation results
        Assert.assertNotNull(confirmation);
        Assert.assertNotNull(confirmation.getConfirmationNumber());
        Assert.assertEquals(confirmationNumber, confirmation.getConfirmationNumber());

        // assert an account contribution was made
        AccountContribution contribution = confirmation
                .getAccountContribution();
        Assert.assertNotNull(contribution);

        // the account number should be '123456789'
        Assert.assertEquals("123456789", contribution.getAccountNumber());

        // the total contribution amount should be 8.00 (8% of 100.00)
        Assert.assertEquals(expectedAmount, contribution.getAmount());

        // each distribution should be 4.00 (as both have a 50% allocation)
        final MonetaryAmount amount4 = new MonetaryAmount(4);
        Assert.assertEquals(amount4, contribution
                .getDistribution("Annabelle").getAmount());
        Assert.assertEquals(amount4, contribution
                .getDistribution("Corgan").getAmount());

        EasyMock.verify(accountRepo, restaurantRepo, rewardRepo);
    }

    /**
     * Usual test values, bad creditCard, but with mocks
     */
    @Test
    public void testRewardForDiningInvalidCreditCard() {
        // createAccountInfo a new dining of 100.00 charged to credit card
        // '1234123412341234' by merchant '123457890' as test input
        Dining dining = Dining.createDining("100.00", "1234123412341235",
                                            "1234567890");


        // Reset account, as makeContribution updates beneficiaries
        Account account = AccountTestData.createTestAccount0();

        EasyMock.expect(accountRepo.findByCreditCard(dining.getCreditCardNumber())).andReturn(null);

        EasyMock.replay(accountRepo, restaurantRepo, rewardRepo);

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
            Assert.assertTrue("creditCardNumber in message:" + edae.getMessage(),
                              edae.getMessage().contains(dining.getCreditCardNumber()));
        }

        EasyMock.verify(accountRepo, restaurantRepo, rewardRepo);
    }


    /**
     * Usual test values, bad merchantNumber, but with mocks
     */
    @Test
    public void testRewardForDiningInvalidMerchantNumber() {
        // createAccountInfo a new dining of 100.00 charged to credit card
        // '1234123412341234' by merchant '123457890' as test input
        Dining dining = Dining.createDining("100.00", "1234123412341234", "1234567891");


        // Reset account, as makeContribution updates beneficiaries
        Account account = AccountTestData.createTestAccount0();


        EasyMock.expect(accountRepo.findByCreditCard(dining.getCreditCardNumber())).andReturn(account);

        EasyMock.expect(restaurantRepo.findByMerchantNumber(dining.getMerchantNumber())).andReturn(null);

        EasyMock.replay(accountRepo, restaurantRepo, rewardRepo);

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

        EasyMock.verify(accountRepo, restaurantRepo, rewardRepo);
    }


    /**
     * Test findRewardFor where reeward exists
     */
    @Test
    public void testFindRewardFor() {
        // createAccountInfo a new dining of 100.00 charged to credit card
        // '1234123412341234' by merchant '123457890' as test input
        Dining dining = Dining.createDining("100.00", "1234123412341234",
                                            "1234567890");

        Account account = AccountTestData.createTestAccount0();
        final MonetaryAmount expectedAmount = new MonetaryAmount(8);
        final AccountContribution expectedContribution = account.makeContribution(expectedAmount);

        String confirmationNumber = new Random().toString();
        RewardConfirmation expectedConfirmation = new RewardConfirmation(
                confirmationNumber, expectedContribution);

        EasyMock.expect(rewardRepo.findConfirmationFor(dining)).andReturn(expectedConfirmation);

        EasyMock.replay(accountRepo, restaurantRepo, rewardRepo);

        RewardConfirmation actualConfirmation = rewardNetwork.findConfirmationFor(dining);

        Assert.assertEquals("rewardConfirmation", expectedConfirmation, actualConfirmation);

        EasyMock.verify(accountRepo, restaurantRepo, rewardRepo);
    }


    /**
     * Test findRewardFor where reward exists
     */
    @Test
    public void testFindRewardForNonExistent() {
        // createAccountInfo a new dining of 100.00 charged to credit card
        // '1234123412341234' by merchant '123457890' as test input
        Dining dining = Dining.createDining("100.00", "1234123412341234",
                                            "1234567890");

        EasyMock.expect(rewardRepo.findConfirmationFor(dining)).andReturn(null);

        EasyMock.replay(accountRepo, restaurantRepo, rewardRepo);

        try {
            RewardConfirmation actualConfirmation = rewardNetwork.findConfirmationFor(dining);
            Assert.fail("expected exception for non-existent confirmation, but got return:" + actualConfirmation);
        } catch (EmptyResultDataAccessException e) {
            EasyMock.verify(accountRepo, restaurantRepo, rewardRepo);
        }
    }
}