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

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;

import ccp.Dining;

public class DiningMapper implements RowMapper<Dining> {
	private Log logger = LogFactory.getLog(getClass());

	@Override
	public Dining mapRow(ResultSet rs, int rowNum) throws SQLException {
		String transactionId = rs.getString("ID");
		String creditCardNumber = rs.getString("CREDIT_CARD_NUMBER");
		String merchantNumber = rs.getString("MERCHANT_NUMBER");
		BigDecimal amount = rs.getBigDecimal("AMOUNT");
		Date date = rs.getDate("DINING_DATE");
		boolean confirmed = rs.getBoolean("CONFIRMED");
		logger.debug("Found Dining with transactionId " + transactionId);
		return new Dining(transactionId, amount, creditCardNumber, merchantNumber, date, confirmed);
	}

}
