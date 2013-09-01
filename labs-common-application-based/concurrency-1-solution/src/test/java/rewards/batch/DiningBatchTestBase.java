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
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

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

    /**
     * Transaction manager
     */
    @Autowired
    private PlatformTransactionManager transactionManager;

    private final Logger logger = Logger.getLogger("rewards");

    /**
     * Method initializing processor and JdbcTemplate
     *
     * @param dataSource
     */
    @Autowired
    void init(DataSource dataSource) {
        logger.info("initializing dataSource");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Clear out rewards
     */
    @After
    public void clearRewards() {
        // clear out the db from the previous test
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        logger.info("clearing rewards:txTemplate=" + txTemplate);
        List<Map<String, Object>> remainingRewards = txTemplate.execute(new TransactionCallback<List<Map<String, Object>>>() {
            @Override
            public List<Map<String, Object>> doInTransaction(TransactionStatus transactionStatus) {
                int deletedCt = jdbcTemplate.update(DELETE_ALL_REWARDS);
                logger.info("deletedCt=" + deletedCt);
                return jdbcTemplate.queryForList("SELECT * FROM T_REWARD");
            }
        });

        Assert.assertEquals("T_REWARDS should be empty:" + remainingRewards, 0, remainingRewards.size());
    }

    /**
     * helper to count rewards
     *
     * @return
     */
    protected int getRewardCount() {
        final DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setReadOnly(true);
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager, transactionDefinition);
        int count = txTemplate.execute(new TransactionCallback<Integer>() {
            @Override public Integer doInTransaction(TransactionStatus transactionStatus) {
                return jdbcTemplate.queryForInt(COUNT_REWARDS_SQL);
            }
        });

        return count;
    }

    /**
     * @return logger
     */
    protected Logger getLogger() {
        return logger;
    }
}
