package rewards.jms;

import common.money.MonetaryAmount;
import common.money.Percentage;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import rewards.Dining;
import rewards.RewardConfirmation;

import javax.jms.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.CoreMatchers.instanceOf;

/**
 * Intergation test for web deployment of JMS RewardsOnline
 * <p/>
 * User: djnorth
 * Date: 05/01/2013
 * Time: 13:22
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class RewardsJmsWebTests {

    /**
     * Send queue name
     */
    private static final String DINING_QUEUE_NAME = "rewards.queue.dining";

    /**
     * Send queue name for XML
     */
    private static final String XML_DINING_QUEUE_NAME = "rewards.queue.xmldining";

    /**
     * Xml test messages
     */
    private static final String XML_DINING_MSG_TEMPLATE = "<dining>\n" +
                                                          "  <amount value=\"__AMOUNT__\" />\n" +
                                                          "  <creditcard number=\"__CREDITCARDNUMBER__\"/>\n" +
                                                          "  <merchant number=\"__MERCHANT_NUMBER__\"/>\n" +
                                                          "  <timestamp>\n" +
                                                          "      <date>__DATE__</date>\n" +
                                                          "      <time>__TIME__</time>\n" +
                                                          "  </timestamp>\n" +
                                                          "</dining>";

    /**
     * Formats for date and time
     */
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger("rewards");

    /**
     * JmsTemplate to send and receive
     */
    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * Confirmation queue
     */
    @Autowired
    private Destination confirmationQueue;


    /**
     * Test 5 dinings sent, then received from server, checking correlation ID is correctly returned
     */
    @Test
    public void testSend5DiningObjectsReceiveConfirmations() throws Exception {

        final String correlationPfx = "OBJ-CORRELATION-";
        final Map<String, Dining> sendDiningMap = prepareSendDiningsMap(correlationPfx);

        for (Map.Entry<String, Dining> diningEntry : sendDiningMap.entrySet()) {

            sendDiningMsg(DINING_QUEUE_NAME, diningEntry.getKey(), diningEntry.getValue());
        }

        final List<Message> receivedMessages = receiveRewardConfirmations(sendDiningMap.size());

        for (Message receivedMessage : receivedMessages) {
            logger.info("receivedMessage=" + receivedMessage);

            Assert.assertThat("receivedMessage should be ObjectMessage", receivedMessage,
                              instanceOf(ObjectMessage.class));

            final ObjectMessage receivedObjectMessage = (ObjectMessage) receivedMessage;
            final Serializable receivedMessagePayload = receivedObjectMessage.getObject();

            Assert.assertThat("receivedMessagePayload should be ObjectMessage", receivedMessagePayload,
                              instanceOf(RewardConfirmation.class));

            final RewardConfirmation rewardConfirmation = (RewardConfirmation) receivedMessagePayload;
            final String correlationId = receivedMessage.getJMSCorrelationID();
            Assert.assertNotNull("received correlationId:receivedMessage=" + receivedMessage, correlationId);

            final Dining correspondingDining = sendDiningMap.get(correlationId);
            Assert.assertNotNull(
                    "corresponding dining should exists in sendDiningMap for correlationId=" + correlationId,
                    correspondingDining);
            final MonetaryAmount expectedAmount =
                    new MonetaryAmount(correspondingDining.getAmount()).multiplyBy(new Percentage(0.08));

            Assert.assertEquals("confirmation amount corresponds to 8% of dining amount", expectedAmount,
                                rewardConfirmation.getAccountContribution().getAmount());
        }
    }


    /**
     * Test 5 dinings sent as XML, then received from server, checking correlation ID is correctly returned
     */
    @Test
    public void testSend5DiningXmlMsgsReceiveConfirmations() throws Exception {

        final String correlationPfx = "OBJ-CORRELATION-";
        final Map<String, Dining> sendDiningMap = prepareSendDiningsMap(correlationPfx);

        for (Map.Entry<String, Dining> diningEntry : sendDiningMap.entrySet()) {
            final Dining dining = diningEntry.getValue();
            final Date dateTime = dining.getDate();
            final String xmlDiningMsg =
                    XML_DINING_MSG_TEMPLATE.replace("__AMOUNT__", Float.toString(dining.getAmount()))
                                           .replace("__CREDITCARDNUMBER__", dining.getCreditCardNumber())
                                           .replace("__MERCHANT_NUMBER__", dining.getMerchantNumber())
                                           .replace("__DATE__", DATE_FORMAT.format(dateTime))
                                           .replace("__TIME__", TIME_FORMAT.format(dateTime));

            sendDiningMsg(XML_DINING_QUEUE_NAME, diningEntry.getKey(), xmlDiningMsg);
        }

        final List<Message> receivedMessages = receiveRewardConfirmations(sendDiningMap.size());

        int msgi = 0;
        for (Message receivedMessage : receivedMessages) {
            logger.info("receivedMessage[" + msgi + "]=" + receivedMessage);

            Assert.assertThat("receivedMessage[" + msgi + "]should be TextMessage", receivedMessage,
                              instanceOf(TextMessage.class));

            final TextMessage receivedTextMessage = (TextMessage) receivedMessage;
            final String receivedMessagePayload = receivedTextMessage.getText();

            Assert.assertNotNull("receivedMessagePayload[" + msgi + "]should not be null or empty", receivedMessagePayload);

            final String correlationId = receivedMessage.getJMSCorrelationID();
            Assert.assertNotNull("received correlationId:receivedMessage[" +  + msgi + "]=" + receivedMessage, correlationId);

            final Dining correspondingDining = sendDiningMap.get(correlationId);
            Assert.assertNotNull(
                    "corresponding dining should exists in sendDiningMap for correlationId=" + correlationId,
                    correspondingDining);
        }
    }


    /**
     * Helper top prepare map of dining objects to send directly or via XML
     *
     * @param correlationPfx
     * @return map keyed on correlationId
     */
    private Map<String, Dining> prepareSendDiningsMap(String correlationPfx) {
        final Map<String, Dining> sendDiningMap = new LinkedHashMap<String, Dining>();

        for (int correlationSeq = 0; correlationSeq < 5; ++correlationSeq) {
            final Dining dining =
                    new Dining(100f + (correlationSeq * 1.3f), "1234123412341234", "1234567890", new Date());
            final String correlationId = correlationPfx + System.currentTimeMillis() + '.' + correlationSeq;
            sendDiningMap.put(correlationId, dining);
        }

        return sendDiningMap;
    }


    /**
     * Helper sending message to dining queue
     *
     * @param destinationName for message
     * @param correlationId   to use
     * @param payload         to send
     */
    private void sendDiningMsg(String destinationName, final String correlationId, Object payload) {

        jmsTemplate.convertAndSend(destinationName, payload, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws JMSException {
                message.setJMSCorrelationID(correlationId);
                message.setJMSReplyTo(confirmationQueue);
                return message;
            }
        });
    }


    /**
     * Helper to receive specified number of messages, or die trying!
     *
     * @param msgCt
     * @return list of messages
     */
    private List<Message> receiveRewardConfirmations(int msgCt) {
        final List<Message> receivedMessages = new ArrayList<Message>(msgCt);
        for (int i = 0; i < msgCt; ++i) {
            receivedMessages.add(jmsTemplate.receive(confirmationQueue));
        }
        return receivedMessages;
    }

}
