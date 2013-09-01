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

package rewards.batch.partition;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import rewards.Dining;

public class DiningRowMapper extends Object implements RowMapper<Dining> {

    @Override
    public Dining mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Dining(rs.getFloat("AMOUNT"),
                          rs.getString("CC_NUMBER"),
                          rs.getString("MERCHANT"),
                          rs.getDate("DINING_DATE"));
    }

}