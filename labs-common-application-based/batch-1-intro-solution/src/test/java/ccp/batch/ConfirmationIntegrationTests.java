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

import static java.util.Collections.singletonList;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ccp.Confirmation;

/**
 * Integration test that checks the implementation and configuration 
 * of the ConfirmationReader and -Updater.  
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:system-test-config.xml")
public class ConfirmationIntegrationTests {
	
	static final String CONFIRMED_SQL = "select CONFIRMED from T_DINING where ID = ?";

	@Autowired ConfirmationReader confirmationReader;
	@Autowired ConfirmationUpdater confirmationUpdater;
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	void initJdbcTemplate(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Test @DirtiesContext
	public void confirm() throws Exception {
		Confirmation confirmation = confirmationReader.read();
		assertNotNull(confirmation);
		String txId = confirmation.getTransactionId();
		
		assertEquals(0, jdbcTemplate.queryForInt(CONFIRMED_SQL, txId));
		confirmationUpdater.write(singletonList(confirmation));
		assertEquals(1, jdbcTemplate.queryForInt(CONFIRMED_SQL, txId));
	}
}
