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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StartLimitExceededException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BatchTests {
	static final String INPUT_RESOURCE_PATH = "input.resource.path";
	
	static final String INPUT_VALID = "classpath:/rewards/batch/diningRequests.csv";
	static final String INPUT_INVALID = "classpath:/rewards/batch/diningRequests-broken.csv";
	
	@Autowired
    private JobLauncher launcher;
	
	@Autowired
    private Job diningRequestsJob;

	// TODO-07 Add an @Autowired annotation to the skippingDiningRequestsJob
	private Job skippingDiningRequestsJob;
	
	@Test
    @Ignore
	// TODO-04 Remove @Ignore and run the test
	public void regularJobSucceedsWithValidInput() throws Exception {
		JobParameters params = new JobParametersBuilder()
			.addDate("start-time", new Date())
			.addString(INPUT_RESOURCE_PATH, INPUT_VALID)
			.toJobParameters();
		JobExecution execution = launcher.run(diningRequestsJob, params);
		assertEquals(ExitStatus.COMPLETED, execution.getExitStatus());
	}

	@Test
    @Ignore
	// TODO-05 Remove @Ignore and run the test
	public void regularJobFailsWithInvalidInput() throws Exception {
		JobParameters params = new JobParametersBuilder()
			.addDate("start-time", new Date())
			.addString(INPUT_RESOURCE_PATH, INPUT_INVALID)
			.toJobParameters();
		JobExecution execution = launcher.run(diningRequestsJob, params);
		assertEquals(ExitStatus.FAILED, execution.getExitStatus());
	}

	@Test
    @Ignore
	// TODO-08 Remove @Ignore and run the test
	public void skippingJobSucceedsWithInvalidInput() throws Exception {
		JobParameters params = new JobParametersBuilder()
			.addDate("start-time", new Date())
			.addString(INPUT_RESOURCE_PATH, INPUT_INVALID)
			.toJobParameters();
		JobExecution execution = launcher.run(skippingDiningRequestsJob, params);
		assertEquals(ExitStatus.COMPLETED, execution.getExitStatus());
	}

	@Test
    @Ignore
	// TODO-10 Remove @Ignore and run the test
	public void restartRegularJobAfterFixingInputSucceeds() throws Exception {
		final Date now = new Date();
		JobParameters invalidParams = new JobParametersBuilder()
			.addDate("start-time", now)
			.addString(INPUT_RESOURCE_PATH, INPUT_INVALID)
			.toJobParameters();
		JobParameters params1 = new ResourcePathHidingJobParameters(invalidParams.getParameters());
		JobExecution execution1 = launcher.run(diningRequestsJob, params1);
		assertEquals(ExitStatus.FAILED, execution1.getExitStatus());
		
		// re-run with valid input, pretending it's the same JobParameters to cause restart:
		JobParameters validParams = new JobParametersBuilder()
			.addDate("start-time", now)
			.addString(INPUT_RESOURCE_PATH, INPUT_VALID)
			.toJobParameters();
		JobParameters params2 = new ResourcePathHidingJobParameters(validParams.getParameters());
		JobExecution execution2 = launcher.run(diningRequestsJob, params2);
		// make sure we were indeed restarting the existing job instance, not starting a new one:
		assertEquals(execution2.getJobInstance(), execution1.getJobInstance());
		assertEquals(ExitStatus.COMPLETED, execution2.getExitStatus());
	}
	
	@Test
    @Ignore
	// TODO-12 Remove @Ignore and run the test
	public void exceedingRestartLimitPreventJobFromRunningAgain() throws Exception {
		JobParameters params = new JobParametersBuilder()
			.addDate("start-time", new Date())
			.addString(INPUT_RESOURCE_PATH, INPUT_INVALID)
			.toJobParameters();
		final int startLimit = 3;
		for (int i = 0; i < startLimit; i++) {
			JobExecution execution = launcher.run(diningRequestsJob, params);
			assertEquals(ExitStatus.FAILED, execution.getExitStatus());
		}
		// should exceed start limit now:
		JobExecution execution = launcher.run(diningRequestsJob, params);
		List<Throwable> failureExceptions = execution.getFailureExceptions();
		assertFalse(failureExceptions.isEmpty());
		Throwable cause = failureExceptions.get(0).getCause().getCause();
		assertEquals(StartLimitExceededException.class, cause.getClass());
	}
	
	/** 
	 * 	Tricks the JdbcJobInstanceDao into thinking we're
	 *	running the same job instance twice as it iterates the Map entries
	 *  via the keySet, since we're not using the same resource path twice for 
	 *  both runs like we would in a real-life scenario.
	 *  Direct access to get the resource path out still works, so the ItemReader
	 *  still knows what file to use.
	 */
	@SuppressWarnings("serial")
	private static class ResourcePathHidingJobParameters extends JobParameters {
		public ResourcePathHidingJobParameters(Map<String,JobParameter> parameters) {
			super(parameters);
		}
		@Override
		public Map<String, JobParameter> getParameters() {
			return new LinkedHashMap<String,JobParameter>(super.getParameters()) {
				@Override
				public Set<String> keySet() {
					Set<String> keySet = super.keySet();
					keySet.remove(INPUT_RESOURCE_PATH);
					return keySet;
				}
			};
		};
	}
}
