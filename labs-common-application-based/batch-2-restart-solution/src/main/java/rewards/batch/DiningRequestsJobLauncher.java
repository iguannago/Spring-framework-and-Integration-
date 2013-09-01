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

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;

public class DiningRequestsJobLauncher {
	private Logger logger = Logger.getLogger(getClass());
	
	private JobLauncher jobLauncher;
	private Job diningRequestsJob;
	
	public DiningRequestsJobLauncher(JobLauncher jobLauncher, Job diningRequestsJob) {
		this.jobLauncher = jobLauncher;
		this.diningRequestsJob = diningRequestsJob;
	}
	
	public void launchJob(File input) throws JobExecutionException {
		JobParameters params = new JobParametersBuilder()
			.addString("input.resource.path", "file:" + input.getAbsolutePath())
			.toJobParameters();
		try {
			JobExecution execution = jobLauncher.run(diningRequestsJob, params);
			logger.info("Ended batch job for file " + input + " with execution " + execution);
		} catch (JobInstanceAlreadyCompleteException e) {
			logger.error("Job for file " + input + " already ran successfully");
		}
	}
}
