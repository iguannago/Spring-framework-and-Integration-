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
