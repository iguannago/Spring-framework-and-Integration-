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

package ccp.batch;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate;

public class QueuePopulator {

	private Log logger = LogFactory.getLog(getClass());

	public QueuePopulator(JmsTemplate jmsTemplate) {
		logger.info("Prepopulating confirmation queue with 150 messages");
		for (int i = 0; i < 150; i++) {
			String uuid = String.format("127cc1d1-cb90-4810-b373-0c66068e3%03d", i);
			Element elConfirmation = new Element("reward-confirmation", ConfirmationReader.NS);
			elConfirmation.addAttribute(new Attribute("dining-transaction-id", uuid));
			jmsTemplate.convertAndSend(new Document(elConfirmation).toXML());
		}

	}

}
