package rewards.messaging.client.sender;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;

import rewards.Dining;

public class StartSender {
	private static final Log LOGGER = LogFactory.getLog(StartSender.class);

	private JmsTemplate template;

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "rewards/messaging/client/sender/sender-config.xml");
		JmsTemplate template = context.getBean(JmsTemplate.class);
		StartSender sender = new StartSender(template);

		Dining dining1 = Dining.createDining("80.93", "1234123412341234", "1234567890");
		Dining dining2 = Dining.createDining("56.12", "1234123412341234", "1234567890");
		Dining dining3 = Dining.createDining("32.64", "1234123412341234", "1234567890");
		Dining dining4 = Dining.createDining("77.05", "1234123412341234", "1234567890");
		Dining dining5 = Dining.createDining("94.50", "1234123412341234", "1234567890");

		sender.sendDining(dining1);
		sender.sendDining(dining2);
		sender.sendDining(dining3);
		sender.sendDining(dining4);
		sender.sendDining(dining5);
		
		context.close();
	}

	public StartSender(JmsTemplate template) {
		this.template = template;
	}

	private void sendDining(Dining dining) {
		LOGGER.info("Sending dining with amount " + dining.getAmount());
		template.convertAndSend(dining);
	}
}
