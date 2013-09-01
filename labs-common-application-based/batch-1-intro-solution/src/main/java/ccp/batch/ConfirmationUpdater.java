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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import ccp.Confirmation;

public class ConfirmationUpdater implements ItemWriter<Confirmation> {
	private Log logger = LogFactory.getLog(getClass());
	private JdbcTemplate jdbcTemplate;

	public ConfirmationUpdater(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void write(final List<? extends Confirmation> items) throws Exception {
		logger.debug("Confirming " + items.size() + " dinings");
		
		jdbcTemplate.batchUpdate("update T_DINING set CONFIRMED=1 where ID=?", 
			new BatchPreparedStatementSetter() {
				public int getBatchSize() {
					// TODO 02a: return the nr of statements in the batch
					return items.size();
				}
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					// TODO 02b: extract the transaction id of the current item
					String id = items.get(i).getTransactionId();
					ps.setString(1, id);
				}
			});
	}
}
