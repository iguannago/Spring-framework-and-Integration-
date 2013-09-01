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

import rewards.Dining;
import rewards.RewardConfirmation;
import rewards.RewardNetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Implementation of DiningBatchProcessor processing CSV file concurrently, using Java ExecutorService
 *
 * @author Dominic North
 */
public class AsyncJavaCSVDiningBatchProcessor extends CSVDiningBatchProcessorBase {

    /**
     * RewardNetwork Service
     */
    private final RewardNetwork rewardNetwork;

    /**
     * ExecutorService
     */
    private final ExecutorService executorService;

    /**
     * Constructor taking service, executor service, date time format string
     *
     * @param rewardNetwork
     * @param executorService
     */
    public AsyncJavaCSVDiningBatchProcessor(RewardNetwork rewardNetwork, ExecutorService executorService,
                                            String dateTimeFormatString) {
        super(dateTimeFormatString);
        this.rewardNetwork = rewardNetwork;
        this.executorService = executorService;
    }


    /**
     * Process batch of dining requests from file corresponding to supplied resource
     *
     * @param batchInput
     * @return count of rewards processed
     * @throws java.io.IOException
     */
    @Override
    public int processBatch(org.springframework.core.io.Resource batchInput) throws IOException {
        int count = 0;

        List<Future<RewardConfirmation>> futureRewardConfirmations = new ArrayList<Future<RewardConfirmation>>();

        BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                batchInput.getInputStream()));

        String csvRecord;
        while ((csvRecord = inputReader.readLine()) != null) {
            //TODO-01 Use executorService to submit records and get a result in a list
            final Dining dining = super.createDiningFromCsv(csvRecord);

            Callable<RewardConfirmation> rewardConfirmationCallable = new Callable<RewardConfirmation>() {
                @Override
                public RewardConfirmation call() throws Exception {
                    return rewardNetwork.rewardAccountFor(dining);
                }
            };
            futureRewardConfirmations.add(executorService.submit(rewardConfirmationCallable));
            ++count;
        }
        //TODO-02 Check each result
        for (Future<RewardConfirmation> futureRewardConfirmation : futureRewardConfirmations) {
            try {
                super.getLogger().info(String.format("rewardConfirmation:", futureRewardConfirmation.get()));
            } catch (Throwable throwable) {
                super.getLogger().error(String.format("rewardConfirmation exception:", throwable));
            }
        }

        return count;
    }
}
