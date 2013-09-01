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

package rewards.ws;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.ws.server.endpoint.annotation.*;
import rewards.Dining;
import rewards.RewardConfirmation;
import rewards.RewardNetwork;
import rewards.ws.marshalling.RewardAccountForDiningResponse;

@Endpoint
public class RewardNetworkEndpoint {

	private static final String NAMESPACE_URI = "http://www.springsource.com/reward-network";

	private RewardNetwork rewardNetwork;

	@Autowired
	public RewardNetworkEndpoint(RewardNetwork rewardNetwork) {
		this.rewardNetwork = rewardNetwork;
	}

	/**
	 * As an alternative to marshalling request, we use the XPathParam annotation for input.
	 *
	 * @param amount
	 * @param creditCardNumber
	 * @param merchantNumber
	 * @return Source with XML response
	 */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "rewardAccountForDiningRequest")
	@Namespace(prefix="r", uri=NAMESPACE_URI)
	public @ResponsePayload RewardAccountForDiningResponse invoke(
			@XPathParam("/r:rewardAccountForDiningRequest//@amount") String amount,
			@XPathParam("/r:rewardAccountForDiningRequest//@creditCardNumber") String creditCardNumber,
			@XPathParam("/r:rewardAccountForDiningRequest//@merchantNumber") String merchantNumber)
			throws ParserConfigurationException {

		Dining dining = Dining.createDining(amount, creditCardNumber,
				merchantNumber);

		RewardConfirmation confirmation = rewardNetwork
				.rewardAccountFor(dining);
		
		RewardAccountForDiningResponse response = new RewardAccountForDiningResponse();
		response.setAccountNumber(confirmation.getAccountContribution().getAccountNumber());
		response.setAmount(confirmation.getAccountContribution().getAmount().asBigDecimal());
		response.setConfirmationNumber(confirmation.getConfirmationNumber());
		
		return response;
	}

}