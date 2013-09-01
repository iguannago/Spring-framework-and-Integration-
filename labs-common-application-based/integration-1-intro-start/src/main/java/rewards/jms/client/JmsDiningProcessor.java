package rewards.jms.client;

import org.springframework.jms.core.JmsTemplate;

import rewards.Dining;
import rewards.DiningProcessor;

/**
 * A processor that sends dining event notifications via JMS, expecting no return
 */
public class JmsDiningProcessor implements DiningProcessor {

	private JmsTemplate jmsTemplate;

    /**
     * Setter for JmsTemplate
     *
     * @param jmsTemplate
     */
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

    /**
     * Processes a dining event, returning nothing.
     *
     * @param dining event
     */
    @Override
    public void process(Dining dining) {
        jmsTemplate.convertAndSend(dining);
    }
}
