package rewards;

/**
 * A batch processor for dining events.
 * 
 * Typical implementations would send notifications to the reward network in order to generate reward confirmations.
 */
public interface DiningProcessor {

	/**
	 * Processes a dining event, returning nothing.
	 * 
	 * @param dining event
	 */
	void process(Dining dining);

}
