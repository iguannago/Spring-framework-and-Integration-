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
