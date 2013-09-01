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

package ccp.batch;

import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/system-test-config.xml")
public class BatchTests {
	static final String NR_OF_CONFIRMED_DININGS = "select count(*) from T_DINING where CONFIRMED=1";

	@Autowired JobLauncherTestUtils testUtils;
	@Autowired QueueViewMBean diningQueueView;
	JdbcTemplate jdbcTemplate;

	@Autowired
	public void initJdbcTemplate(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Test @DirtiesContext // updates the in-memory database, so dirty the context
	public void runBatch() throws Exception {
		// TODO-06 Use the JobLauncherTestUtils to launch the job.
		// Assert that the resulting JobExecution has an exitStatus of ExitStatus.COMPLETED
		// and use the jdbcTemplate to assert that the number of confirmed dinings in the database
		// is now 150. (the same as the number of confirmation messages that were on the queue.)
        JobExecution execution = testUtils.launchJob();
		assertEquals("expected job exit status", ExitStatus.COMPLETED, execution.getExitStatus());

		int confirmedDiningsCount = jdbcTemplate.queryForInt(NR_OF_CONFIRMED_DININGS);
		assertEquals("confirmed dinings", 150, confirmedDiningsCount);

		// TODO-12 Assert that the batch sent 150 messages using the diningQueueView's queueSize property
        assertEquals("sent messages", 150, diningQueueView.getQueueSize());
	}
}
