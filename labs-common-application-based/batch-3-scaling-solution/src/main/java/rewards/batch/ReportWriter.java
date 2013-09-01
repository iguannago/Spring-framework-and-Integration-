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

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import rewards.RewardConfirmation;

// TODO 01: Export the instance of this class as an MBean; use the @ManagedResource
// annotation, and use an objectName of "spring.application:application=batch-3-scaling-solution,type=Writer"
@ManagedResource(objectName = "spring.application:application=batch-3-scaling-solution,type=Writer")
public class ReportWriter implements ItemWriter<RewardConfirmation> {

	// TODO 02: Make this field a managed attribute by adding getters and
	// setters and annotating them with @ManagedAttribute
	// Notice how this field is used in write() to generate an exception
	private volatile int failOnConfirmationNumber = Integer.MAX_VALUE;

	private volatile int sleepPerItemMs = 0;

	/**
	 * @return the sleepPerItemMs
	 */
	@ManagedAttribute
	public int getSleepPerItemMs() {
		return sleepPerItemMs;
	}

	/**
	 * @param sleepPerItemMs
	 *            the sleepPerItemMs to set
	 */
	@ManagedAttribute
	public void setSleepPerItemMs(int sleepPerItemMs) {
		this.sleepPerItemMs = sleepPerItemMs;
	}

	/**
	 * @return the failOnConfirmationNumber
	 */
	@ManagedAttribute
	public int getFailOnConfirmationNumber() {
		return failOnConfirmationNumber;
	}

	/**
	 * @param failOnConfirmationNumber
	 *            the failOnConfirmationNumber to set
	 */
	@ManagedAttribute
	public void setFailOnConfirmationNumber(int failOnConfirmationNumber) {
		this.failOnConfirmationNumber = failOnConfirmationNumber;
	}

	private int count;

	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void write(List<? extends RewardConfirmation> items)
			throws Exception {
		logger.debug("wrote " + items.size()
				+ " confirmations, last confirmation nr = "
				+ items.get(items.size() - 1).getConfirmationNumber());
		count += items.size();

		if (count > failOnConfirmationNumber) {
			throw new Exception("Planned exception, processed " + count
					+ " requested failure at " + failOnConfirmationNumber);
		}
		// TODO 03: Add Thread.sleep(1000 * items.size()) to slow down the job
		if (sleepPerItemMs > 0) {
			Thread.sleep(sleepPerItemMs * items.size());
		}
	}

}
