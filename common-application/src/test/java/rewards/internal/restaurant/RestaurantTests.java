package rewards.internal.restaurant;

import static org.junit.Assert.assertEquals;

import common.money.MonetaryAmount;
import common.money.Percentage;
import org.junit.Before;
import org.junit.Test;

import rewards.Dining;
import rewards.internal.account.Account;
import rewards.internal.account.AccountTestData;

/**
 * Unit tests for exercising the behavior of the Restaurant aggregate entity. A restaurant calculates a benefit to award
 * to an account for dining based on an availability policy and benefit percentage.
 */
public class RestaurantTests {

	private Restaurant restaurant;

	private Account account;

	private Dining dining;

	@Before
	public void setUp() {
		// configure the restaurant, the object being tested
		restaurant = new Restaurant("1234567890", "AppleBee's");
		restaurant.setBenefitPercentage(new Percentage(0.08));
		restaurant.setBenefitAvailabilityPolicy(BenefitAvailabilityPolicy.ALWAYS_AVAILABLE);
		// configure supporting objects needed by the restaurant
		account = AccountTestData.createTestAccount0();
		dining = Dining.createDining("100.00", "1234123412341234", "1234567890");
	}

	@Test
	public void testCalculateBenefitFor() {
		MonetaryAmount benefit = restaurant.calculateBenefitFor(account, dining);
		// assert 8.00 eligible for reward
		assertEquals("benefit", new MonetaryAmount(8), benefit);
	}

	@Test
	public void testNoBenefitAvailable() {
		// configure stub that always returns false
		restaurant.setBenefitAvailabilityPolicy(BenefitAvailabilityPolicy.NEVER_AVAILABLE);
		MonetaryAmount benefit = restaurant.calculateBenefitFor(account, dining);
		// assert zero eligible for reward
		assertEquals("benefit", MonetaryAmount.zero(), benefit);
	}

	
}