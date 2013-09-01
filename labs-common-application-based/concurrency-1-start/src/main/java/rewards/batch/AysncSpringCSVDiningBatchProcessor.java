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

import org.springframework.core.io.Resource;
import rewards.Dining;
import rewards.RewardConfirmation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * DiningBatchProcessor implementation using Spring Async call via injected bean
 *
 * @author Dominic North
 */
public class AysncSpringCSVDiningBatchProcessor extends CSVDiningBatchProcessorBase {

    /**
     * Our aysnc processor
     */
    private final RewardNetworkAsync rewardNetworkAsync;


    /**
     * Constructor taking async reward network service and date-time format
     *
     * @param rewardNetworkAsync
     * @param dateTimeFormatString
     */
    public AysncSpringCSVDiningBatchProcessor(RewardNetworkAsync rewardNetworkAsync, String dateTimeFormatString) {
        super(dateTimeFormatString);
        this.rewardNetworkAsync = rewardNetworkAsync;
    }


    /**
     * Process batch of dining requests from file corresponding to supplied resource
     *
     * @param batchInput
     * @return count of rewards processed
     * @throws java.io.IOException
     */
    @Override
    public int processBatch(Resource batchInput) throws IOException {
        int count = 0;

        BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                batchInput.getInputStream()));
        List<Future<RewardConfirmation>> futureRewardConfirmations = new ArrayList<Future<RewardConfirmation>>();

        String csvRecord;
        while ((csvRecord = inputReader.readLine()) != null) {
            // TODO-07 Use rewardNetworkAsync to submit records
            final Dining dining = createDiningFromCsv(csvRecord);
            futureRewardConfirmations.add(rewardNetworkAsync.submitRewardAccountFor(dining));
            ++count;
        }

        // TODO-08 Check each result
        for (Future<RewardConfirmation> futureRewardConfirmation : futureRewardConfirmations) {
             try {
                 getLogger().info("RewardConfirmation: " + futureRewardConfirmation.get());

             } catch (Throwable throwable) {
                 getLogger().error("RewardConfirmation: " + throwable);
             }
        }

        return count;
    }

}
