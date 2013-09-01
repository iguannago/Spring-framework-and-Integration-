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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Dominic North
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ConfiguredScheduledAsyncSpringCSVDiningBatchProcessorTests extends DiningBatchTestBase {

    @Value("#{testProps.fixedDelayTimeMs}")
    private int fixedDelayTimeMs;
    
    @Value("#{testProps.expectedBatchSize}")
    private int expectedBatchSize;
    
    @Value("#{testProps.maxElapsedTimeMs}")
    private int maxSleepMs;
    
    @Value("#{testProps.sleepIntervalMs}")
    private int sleepIntervalMs;

    /**
     * Test scheduled run after
     */
    @Test
    public void testScheduledRun() throws Exception {
        Assert.assertThat(getRewardCount(), CoreMatchers.equalTo(0));

        final long start = System.currentTimeMillis();
        Thread.sleep(fixedDelayTimeMs);
        final long sleepMs = System.currentTimeMillis() - start;
        getLogger().info("after sleep for " + fixedDelayTimeMs + "ms: elapsed time=" + sleepMs + "ms");

        int currentSleepMs = 0;
        while (getRewardCount() < expectedBatchSize && maxSleepMs > currentSleepMs) {
            Thread.sleep(sleepIntervalMs);
            currentSleepMs =+ sleepIntervalMs;
        }

        final long totalMs = System.currentTimeMillis() - start;

        // ensure that all batch records actually made it into the database
        Assert.assertThat(getRewardCount(), CoreMatchers.equalTo(expectedBatchSize));
        getLogger().info("finished after " + totalMs + "ms");
    }

}
