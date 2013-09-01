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

import java.text.SimpleDateFormat;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import ccp.Dining;

public class DiningConverter implements MessageConverter {

	private static final String NS = "http://www.springsource.com/dining-request"; 

	@Override
	public Object fromMessage(Message message) throws JMSException,	MessageConversionException {
		throw new UnsupportedOperationException("DiningConverter can only generate Messages, not handle them");
	}

	@Override
	public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
		Dining dining = (Dining) object;

		Element elDining = new Element("dining", NS);
		elDining.addAttribute(new Attribute("transaction-id", dining.getTransactionId()));
		Element elAmount = new Element("amount", NS);
		elDining.appendChild(elAmount);
		elAmount.addAttribute(new Attribute("value", dining.getAmount().toPlainString()));
		Element elCreditCard = new Element("creditcard", NS);
		elDining.appendChild(elCreditCard);
		elCreditCard.addAttribute(new Attribute("number", dining.getCreditCardNumber()));
		Element elMerchant = new Element("merchant", NS);
		elDining.appendChild(elMerchant);
		elMerchant.addAttribute(new Attribute("number", dining.getMerchantNumber()));
		Element elTimestamp = new Element("timestamp", NS);
		elDining.appendChild(elTimestamp);
		Element elDate = new Element("date", NS);
		elTimestamp.appendChild(elDate);
		elDate.appendChild(new SimpleDateFormat("yyyy-MM-dd").format(dining.getDate()));
		Element elTime = new Element("time", NS);
		elTimestamp.appendChild(elTime);
		elTime.appendChild(new SimpleDateFormat("HH:mm:ss").format(dining.getDate()));

		Document doc = new Document(elDining);

		TextMessage message = session.createTextMessage();
		message.setText(doc.toXML());

		return message;
	}

}
