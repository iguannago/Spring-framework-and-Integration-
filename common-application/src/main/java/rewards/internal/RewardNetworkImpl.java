package rewards.internal;

import common.money.MonetaryAmount;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rewards.AccountContribution;
import rewards.Dining;
import rewards.RewardConfirmation;
import rewards.RewardNetwork;
import rewards.internal.account.Account;
import rewards.internal.account.AccountRepository;
import rewards.internal.restaurant.Restaurant;
import rewards.internal.restaurant.RestaurantRepository;
import rewards.internal.reward.RewardRepository;

/**
 * Rewards an Account for Dining at a Restaurant.
 * 
 * The sole Reward Network implementation. This object is an application-layer service responsible for coordinating with
 * the domain-layer to carry out the process of rewarding benefits to accounts for dining.
 * 
 * Said in other words, this class implements the "reward account for dining" use case.
 */
@Service("rewardNetwork")
public class RewardNetworkImpl implements RewardNetwork {

	private AccountRepository accountRepository;

	private RestaurantRepository restaurantRepository;

	private RewardRepository rewardRepository;

	/**
	 * Creates a new reward network.
	 * @param accountRepository the repository for loading accounts to reward
	 * @param restaurantRepository the repository for loading restaurants that determine how much to reward
	 * @param rewardRepository the repository for recording a record of successful reward transactions
	 */
	public RewardNetworkImpl(AccountRepository accountRepository, RestaurantRepository restaurantRepository,
			RewardRepository rewardRepository) {
		this.accountRepository = accountRepository;
		this.restaurantRepository = restaurantRepository;
		this.rewardRepository = rewardRepository;
	}

    /**
     * Create reward from dining.
     * If the account is not found, throw 
     *
     * @param dining a charge made to a credit card for dining at a restaurant
     * @return reward confirmation
     */
	@Transactional
    @Override
	public RewardConfirmation rewardAccountFor(Dining dining) {
		Account account = accountRepository.findByCreditCard(dining.getCreditCardNumber());
        if (account == null) {
            throw new EmptyResultDataAccessException("account not found for creditCardNumber:dining=" + dining, 1);
        }

		Restaurant restaurant = restaurantRepository.findByMerchantNumber(dining.getMerchantNumber());
        if (restaurant == null) {
            throw new EmptyResultDataAccessException("restaurant not found for merchantNumber:dining=" + dining, 1);
        }

        MonetaryAmount amount = restaurant.calculateBenefitFor(account, dining);
		AccountContribution contribution = account.makeContribution(amount);
		return rewardRepository.confirmReward(contribution, dining);
	}

    /**
     * Find the reward corresponding to supplied dining information
     * <p/>
     * If reward not found, then an exception is thrown
     *
     * @param dining
     * @return corresponding confirmation
     */
    @Transactional(readOnly = true)
    @Override
    public RewardConfirmation findConfirmationFor(Dining dining) {
        RewardConfirmation rewardConfirmation = rewardRepository.findConfirmationFor(dining);
        if (rewardConfirmation == null) {
            throw new EmptyResultDataAccessException("no rewardConfirmation found for dining:" + dining, 1);
        }

        return rewardConfirmation;
    }
}