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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ccp.Dining;

/**
 * Simple integration test that checks if the unconfirmedDiningsReader
 * has been correctly configured as a Spring bean to help with the lab. 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:system-test-config.xml")
public class UnconfirmedDiningsReaderTests {
	@Autowired JdbcPagingItemReader<Dining> diningsReader;
	
	@Test @DirtiesContext
	public void readUnconfirmedDinings() throws Exception {
		Dining dining = diningsReader.read();
		assertNotNull(dining);
		assertEquals("127cc1d1-cb90-4810-b373-0c66068e3000", dining.getTransactionId());
	}
}
