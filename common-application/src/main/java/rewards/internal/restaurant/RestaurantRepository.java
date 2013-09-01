package rewards.internal.restaurant;

/**
 * Loads restaurant aggregates. Called by the reward network to find and reconstitute Restaurant entities from an
 * external form such as a set of RDMS rows.
 * 
 * Objects returned by this repository are guaranteed to be fully-initialized and ready to use.
 */
public interface RestaurantRepository {

    /**
     * Find the restaurant for supplied merchantNumber
     *
     * @param merchantNumber the merchant number
     * @return restaurant, or null if not found
     */
	public Restaurant findByMerchantNumber(String merchantNumber);
}
