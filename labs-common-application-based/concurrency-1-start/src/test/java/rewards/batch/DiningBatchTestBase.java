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

import org.apache.log4j.Logger;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Base class for tests working with dining batches, providing common initialization of JdbcTemplate,
 * and corresponding helper methods for counting and clearing rewards.
 *
 * @author Dominic North
 */
public class DiningBatchTestBase {
    private static final String COUNT_REWARDS_SQL  = "select count(*) from T_REWARD";
    private static final String DELETE_ALL_REWARDS = "delete from T_REWARD";
    /**
     * JdbcTemplate used to validate results
     */
    private JdbcTemplate jdbcTemplate;
    private final Logger logger = Logger.getLogger("rewards");

    /**
     * Method initializing processor and JdbcTemplate
     *
     * @param dataSource
     */
    @Autowired void init(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Clear out rewards
     */
    @Before
    public void clearRewards() {
        // clear out the db from the previous test
        jdbcTemplate.update(DELETE_ALL_REWARDS);
    }

    /**
     * helper to count rewards
     *
     * @return
     */
    protected int getRewardCount() {
        return jdbcTemplate.queryForInt(COUNT_REWARDS_SQL);
    }

    /**
     * @return logger
     */
    protected Logger getLogger() {
        return logger;
    }
}
