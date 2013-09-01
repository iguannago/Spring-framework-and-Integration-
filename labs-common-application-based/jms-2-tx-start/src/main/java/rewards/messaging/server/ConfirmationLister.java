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

package rewards.messaging.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

public class ConfirmationLister {
	private JdbcTemplate jdbcTemplate;

	public ConfirmationLister(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<String> listAllConfirmations() {
		List<String> confirmations = new ArrayList<String>();
		List<Map<String,Object>> list = jdbcTemplate.queryForList("select * from T_REWARD");
		for (Map<String, Object> map : list) {
			StringBuilder b = new StringBuilder();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				b.append(entry.getKey())
				 .append('=')
				 .append(entry.getValue())
				 .append(", ");
			}
			confirmations.add(b.toString());
		}
		return confirmations;
	}

	public int getNrOfConfirmations() {
		return jdbcTemplate.queryForInt("select count(0) from T_REWARD");
	}
}
