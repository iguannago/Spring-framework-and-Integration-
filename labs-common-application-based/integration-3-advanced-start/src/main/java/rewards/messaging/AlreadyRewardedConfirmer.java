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

import org.springframework.util.Assert;
import rewards.ConfirmationProcessor;
import rewards.Dining;
import rewards.RewardConfirmation;
import rewards.internal.reward.RewardRepository;

/**
 * Message Endpoint that checks if a Dining was already processed. If so it
 * short circuits the message flow and sends the existing confirmation directly
 * to the confirmationProcessor, bypassing the {@link rewards.RewardNetwork}.
 */
public class AlreadyRewardedConfirmer {

	private final RewardRepository rewardRepository;
	private final ConfirmationProcessor confirmationProcessor;

	public AlreadyRewardedConfirmer(RewardRepository rewardRepository,
                                    ConfirmationProcessor confirmationProcessor) {
		Assert.notNull(rewardRepository);
		Assert.notNull(confirmationProcessor);

		this.confirmationProcessor = confirmationProcessor;
		this.rewardRepository = rewardRepository;
	}

	/**
	 * Check if the passed in dining was already processed and if so resend the
	 * confirmation and return null. Return the passed in dining otherwise.
	 */
	public Dining sendConfirmationForExistingDining(Dining dining) {
		RewardConfirmation existingConfirmation = rewardRepository
				.findConfirmationFor(dining);
		if (existingConfirmation != null) {
			confirmationProcessor.process(existingConfirmation);
			return null; // prevent further processing of the dining
		}
		return dining;
	}
}
