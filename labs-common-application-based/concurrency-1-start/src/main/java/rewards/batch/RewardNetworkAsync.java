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

package rewards.batch;

import rewards.Dining;
import rewards.RewardConfirmation;

import java.util.concurrent.Future;

/**
 * Interface for async calls to RewardNetwork service
 *
 * @author Dominic North
 */
public interface RewardNetworkAsync {

    /**
     * Do reward an account for dining asynchronously.
     * <p/>
     * For a dining to be eligible for reward: - It must have been paid for by a registered credit card of a valid
     * member account in the network. - It must have taken place at a restaurant participating in the network.
     *
     * @param dining a charge made to a credit card for dining at a restaurant
     * @return Future for confirmation of the reward
     */
    Future<RewardConfirmation> submitRewardAccountFor(Dining dining);
}
