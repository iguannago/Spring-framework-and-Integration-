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

package rewards.jms.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.subethamail.wiser.Wiser;
import rewards.ConfirmationLogger;
import rewards.Dining;
import rewards.DiningProcessor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests the Dining batch processor
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class DiningBatchProcessorTests {

    @Autowired
    private DiningProcessor diningBatchProcessor;

    @Autowired
    private ConfirmationLogger confirmationLogger;

    @Autowired
    private Wiser mailServer;

    @Test
    @DirtiesContext
    public void testBatch() throws Exception {
        Dining dining1 = Dining.createDining("80.93", "1234123412341234", "1234567890");
        Dining dining2 = Dining.createDining("56.12", "1234123412341234", "1234567890");
        Dining dining3 = Dining.createDining("32.64", "1234123412341234", "1234567890");
        Dining dining4 = Dining.createDining("77.05", "1234123412341234", "1234567890");
        Dining dining5 = Dining.createDining("94.50", "1234123412341234", "1234567890");

        List<Dining> batch = new ArrayList<Dining>();
        batch.add(dining1);
        batch.add(dining2);
        batch.add(dining3);
        batch.add(dining4);
        batch.add(dining5);

        for (Dining dining : batch) {
            diningBatchProcessor.process(dining);
        }

        waitForBatch(batch.size(), 30000);

        assertEquals(batch.size(), confirmationLogger.getConfirmations().size());

        assertEquals(5, mailServer.getMessages().size());
        assertEquals("keithd@gmail.com", mailServer.getMessages().get(0).getEnvelopeReceiver());
    }

    private void waitForBatch(int batchSize, int timeout) throws InterruptedException {
        int sleepTime = 100;
        while (confirmationLogger.getConfirmations().size() < batchSize && timeout > 0) {
            Thread.sleep(sleepTime);
            timeout -= sleepTime;
        }
    }
}
