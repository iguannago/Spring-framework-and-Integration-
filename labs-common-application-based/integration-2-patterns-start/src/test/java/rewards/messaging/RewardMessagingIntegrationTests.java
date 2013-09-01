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

package rewards.messaging;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.MessageRejectedException;
import org.springframework.integration.MessagingException;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.core.MessageHandler;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import rewards.Dining;
import rewards.RewardConfirmation;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class RewardMessagingIntegrationTests {

    @Autowired
    private MessagingTemplate template;

    // errorChannel is created in infrastructure
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private PublishSubscribeChannel errorChannel;

    @Test
    public void sendDiningTwice() throws Exception {
        Dining dining = Dining.createDining("100.00", "1234123412341234", "1234567890");
        template.convertAndSend("dinings", dining);

        Object payload1 = template.receiveAndConvert("confirmations");
        assertTrue("payload1=" + payload1, payload1 instanceof RewardConfirmation);
        RewardConfirmation confirmation1 = (RewardConfirmation) payload1;

        // TODO-01 remove try (fail) catch (assertTrue) and run test
        template.convertAndSend("dinings", dining);

        // TODO-11a Receive payload2 as above, as confirmation2, check isEqualOrDuplicate to confirmation1
        Object payload2 = null;
    }

    @Test
    @Ignore // TODO-12 remove @Ignore to run test
    public void sendInvalidDining() throws Exception {
        ErrorHandler handler = new ErrorHandler();
        errorChannel.subscribe(handler);

        Dining invalidDining = new Dining(-1f, null, null, (Date) null);
        template.convertAndSend("dinings", invalidDining);

        Thread.sleep(1000);
        assertNotNull("No error message received", handler.msg);
        assertTrue(handler.msg.getPayload() instanceof MessageRejectedException);
    }

    static class ErrorHandler implements MessageHandler {
        volatile Message<?> msg;

        public void handleMessage(Message<?> message) throws MessagingException {
            msg = message;
        }
    }

}
