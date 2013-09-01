package rewards.jms.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import rewards.Dining;
import rewards.DiningProcessor;
import rewards.RewardConfirmationLogger;

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
    private RewardConfirmationLogger confirmationLogger;

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

        // TODO-08 Invoke the DiningProcessor to send dining list via JMS

        for (Dining dining : batch) {
            diningBatchProcessor.process(dining);
        }

        waitForBatch(batch.size(), 30000);

        // TODO-09 Assert that the confirmation logger has received the entire batch
        assertEquals(batch.size(), confirmationLogger.getConfirmations().size());
    }

    private void waitForBatch(int batchSize, int timeout) throws InterruptedException {
        int sleepTime = 100;
        while (confirmationLogger.getConfirmations().size() < batchSize && timeout > 0) {
            Thread.sleep(sleepTime);
            timeout -= sleepTime;
        }
    }
}
