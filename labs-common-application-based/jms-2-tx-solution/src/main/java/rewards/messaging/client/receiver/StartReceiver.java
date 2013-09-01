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

package rewards.messaging.client.receiver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;

import rewards.RewardConfirmation;

public class StartReceiver {

	private static final Log LOGGER = LogFactory.getLog(StartReceiver.class);

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "rewards/messaging/client/receiver/receiver-config.xml");
		JmsTemplate template = context.getBean(JmsTemplate.class);

		RewardConfirmation confirmation = null;
		do {
			confirmation = (RewardConfirmation) template.receiveAndConvert();
			if (confirmation != null) LOGGER.info("received confirmation: " + confirmation);
		} while (confirmation != null);
		
		context.close();
	}
}
