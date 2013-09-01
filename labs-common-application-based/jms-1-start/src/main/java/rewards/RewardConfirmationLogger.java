package rewards;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple logger for reward confirmations.
 */
public class RewardConfirmationLogger implements ConfirmationLogger {

    private static final Log logger = LogFactory.getLog(RewardConfirmationLogger.class);

    private List<RewardConfirmation> confirmations = new ArrayList<RewardConfirmation>();

    /**
     * Log confirmation (if logging at suitable level)
     *
     * @param rewardConfirmation
     */
    @Override
    public void log(RewardConfirmation rewardConfirmation) {
        this.confirmations.add(rewardConfirmation);
        if (logger.isInfoEnabled()) {
            logger.info("received confirmation: " + rewardConfirmation);
        }
    }

    /**
     * Method to get unmodifiable copy of confirmations
     *
     * @return confirmations
     */
    @Override public List<RewardConfirmation> getConfirmations() {
        return Collections.unmodifiableList(confirmations);
    }
}
