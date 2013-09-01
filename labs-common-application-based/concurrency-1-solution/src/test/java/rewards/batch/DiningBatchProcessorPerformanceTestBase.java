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

package rewards.batch;

import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;

/**
 * Base class for tests using injected dining batch processor directly.
 *
 * @author Dominic North
 */
public abstract class DiningBatchProcessorPerformanceTestBase extends DiningBatchTestBase {
    private static final long MAX_SLA_MILLIS = 30000;

    /**
     * Processor under test
     */
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private DiningBatchProcessor diningBatchProcessor;

    /**
     * Test with small batch
     *
     * @throws java.io.IOException
     */
    @Test
    @DirtiesContext
    public void testProcessSmallBatchIsFastEnough() throws IOException {
        Resource diningBatchCsvFile = new ClassPathResource("dining-input-small.csv");

        doTimedBatchProcessing(diningBatchCsvFile, 5);
    }

    /**
     * Test with large batch
     *
     * @throws java.io.IOException
     */
    @Test
    @DirtiesContext
    public void testProcessLargeBatchIsFastEnough() throws IOException {
        Resource diningBatchCsvFile = new ClassPathResource("dining-input-large.csv");

        doTimedBatchProcessing(diningBatchCsvFile, 1000);
    }

    /**
     * helper to run times test
     *
     * @param csvDiningBatch
     * @param expectedBatchSize
     * @throws java.io.IOException
     */
    private void doTimedBatchProcessing(Resource csvDiningBatch, int expectedBatchSize) throws IOException {
        Assert.assertThat(getRewardCount(), CoreMatchers.equalTo(0));

        long start = System.currentTimeMillis();

        int count = diningBatchProcessor.processBatch(csvDiningBatch);

        long totalMs = System.currentTimeMillis() - start;

        // ensure that all batch records actually made it into the database
        Assert.assertThat("count", count, CoreMatchers.equalTo(expectedBatchSize));
        Assert.assertThat("getRewardCount()", getRewardCount(), CoreMatchers.equalTo(expectedBatchSize));

        // was the batch processed within the required amount of time?
        Assert.assertTrue("took too long! max ms: " + MAX_SLA_MILLIS + "; actual ms: " + totalMs,
                          totalMs < MAX_SLA_MILLIS);
        getLogger().info("finished after " + totalMs + "ms");
    }

}
