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

package rewards.messaging.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.JmsUtils;
import org.springframework.transaction.annotation.Transactional;
import rewards.Dining;
import rewards.RewardConfirmation;
import rewards.RewardNetwork;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

public class DiningListenerImpl implements DiningListener {

    private Log log = LogFactory.getLog(getClass());

    private RewardNetwork rewardNetwork;
    private JmsTemplate   jmsTemplate;

    private boolean causeErrorAfterReceiving  = false;
    private boolean causeErrorAfterProcessing = false;
    private boolean causeErrorAfterSending    = false;

    public DiningListenerImpl(RewardNetwork rewardNetwork, JmsTemplate jmsTemplate) {
        this.rewardNetwork = rewardNetwork;
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public boolean isCauseErrorAfterReceiving() {
        return causeErrorAfterReceiving;
    }

    @Override
    public void setCauseErrorAfterReceiving(boolean flag) {
        this.causeErrorAfterReceiving = flag;
    }

    @Override
    public boolean isCauseErrorAfterProcessing() {
        return causeErrorAfterProcessing;
    }

    @Override
    public void setCauseErrorAfterProcessing(boolean flag) {
        this.causeErrorAfterProcessing = flag;
    }

    @Override
    public boolean isCauseErrorAfterSending() {
        return causeErrorAfterSending;
    }

    @Override
    public void setCauseErrorAfterSending(boolean flag) {
        this.causeErrorAfterSending = flag;
    }

    @Override
    @Transactional
    public void onMessage(Message message) {
        try {
            Dining dining = (Dining) ((ObjectMessage) message).getObject();
            logMessage(message, dining);
            if (causeErrorAfterReceiving) {
                throw new RuntimeException("error after receiving dining with amount " + dining.getAmount());
            }
            RewardConfirmation confirmation = this.rewardNetwork.rewardAccountFor(dining);
            if (causeErrorAfterProcessing) {
                throw new RuntimeException("error after processing dining with amount " + dining.getAmount());
            }
            jmsTemplate.convertAndSend(confirmation);
            log.debug("Sent response with confirmation nr " + confirmation.getConfirmationNumber());
            if (causeErrorAfterSending) {
                throw new RuntimeException("error after sending confirmation for dining with amount " + dining.getAmount());
            }
        } catch (JMSException e) {
            throw JmsUtils.convertJmsAccessException(e);
        }
    }

    private void logMessage(Message message, Dining dining) throws JMSException {
        if (log.isDebugEnabled()) {
            String msg = "Received Dining with amount " + dining.getAmount();
            if (message.getJMSRedelivered()) {
                int nrOfDeliveries = message.getIntProperty("JMSXDeliveryCount");
                msg += " (redelivered " + (nrOfDeliveries - 1) + " times)";
            }
            log.debug(msg);
        }
    }
}
