package rewards.batch;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import rewards.Dining;
import rewards.RewardConfirmation;
import rewards.RewardNetwork;

import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: d.crespo@london.net-a-porter.com
 * Date: 25/07/2013
 * Time: 00:12
 * To change this template use File | Settings | File Templates.
 */
public class RewardNetworkAsyncImpl implements RewardNetworkAsync {

    private final RewardNetwork rewardNetwork;

    public RewardNetworkAsyncImpl(RewardNetwork rewardNetwork) {
        this.rewardNetwork = rewardNetwork;
    }

    @Override
    @Async
    public Future<RewardConfirmation> submitRewardAccountFor(Dining dining) {
        return new AsyncResult<RewardConfirmation>
                (rewardNetwork.rewardAccountFor(dining));
    }
}
