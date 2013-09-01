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

import java.io.IOException;

/**
 * Implementation of ConfiguredDiningBatchProcessor delegating to DiningBatchProcessor, using configured resource
 *
 * @author Dominic North
 */
public class DelegatingConfiguredDiningBatchProcessor implements ConfiguredDiningBatchProcessor {

    /**
     * DiningBatchProcessor
     */
    private final DiningBatchProcessor diningBatchProcessor;

    /**
     * resource to process
     */
    private final Resource batchInput;


    /**
     * Constructor taking resource for batchfile and processor to which we delegate
     *
     * @param diningBatchProcessor
     * @param batchInput
     */
    public DelegatingConfiguredDiningBatchProcessor(DiningBatchProcessor diningBatchProcessor, Resource batchInput) {
        this.diningBatchProcessor = diningBatchProcessor;
        this.batchInput = batchInput;
    }


    /**
     * Process batch of dining requests from file corresponding to configured resource
     *
     * @return count of rewards processed
     * @throws java.io.IOException
     */
    @Override
    public int processConfiguredBatch() throws IOException {
        return diningBatchProcessor.processBatch(batchInput);
    }
}
