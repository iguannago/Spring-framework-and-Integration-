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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility class to add extra accounts for bulk test
 *
 * @author Dominic North
 */
public class AccountTestPopulator implements InitializingBean {

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger("rewards");

    /**
     * JdbcTemplate
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Transaction template
     */
    private final TransactionTemplate transactionTemplate;

    private final int          startSeq  = 21;
    private final int          maxSeq    = 999;
    private final String       ccPfx     = "1234123412340";
    private final String       anPfx     = "123450";
    private final String       namePfx   = "John Doe ";
    private final String       emailPfx  = "acc";
    private final String       emailSfx  = "@gmail.com";
    private final NumberFormat sfxFormat = new DecimalFormat("000");
    private final Date dob;

    /**
     * Constructor taking dataSource
     *
     * @param dataSource
     * @param transactionManager
     */
    public AccountTestPopulator(DataSource dataSource, PlatformTransactionManager transactionManager) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        Calendar initialDob = Calendar.getInstance();
        initialDob.set(1970, 1, 1);
        this.dob = initialDob.getTime();
    }

    /**
     * Initialise database with additional accounts
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                for (int seq = startSeq; seq <= maxSeq; ++seq) {

                    final String sfx = sfxFormat.format(seq);
                    final String number = anPfx + sfx;
                    final String name = namePfx + sfx;
                    final String email = emailPfx + sfx + emailSfx;
                    final boolean rcvNewsletter = (seq % 2 == 0);
                    final boolean rcvEmail = (seq % 3 == 0);
                    final String cc = ccPfx + sfx;

                    logger.debug("populating accounts[1]:seq=" + seq + ":number=" + number + ":name=" + name + ":email=" + email +
                                ":rcvNewsletter" + rcvNewsletter + ":rcvEmail=" + rcvEmail);

                    final String insertAccSql =
                            "insert into T_ACCOUNT(NUMBER, NAME, DATE_OF_BIRTH, EMAIL, REWARDS_NEWSLETTER, " +
                            "MONTHLY_EMAIL_UPDATE, VERSION) " +
                            "values (?, ?, ?, ?, ?, ?, 0)";
                    jdbcTemplate.update(insertAccSql, number, name, dob, email, rcvNewsletter, rcvEmail);

                    final String queryAccIdSql = "select id from T_ACCOUNT where NUMBER = ?";
                    final int accid = jdbcTemplate.queryForInt(queryAccIdSql, number);

                    logger.debug("populating accounts[2]:seq=" + seq + ":accid=" + accid + ":cc=" + cc);

                    final String insertCCSql =
                            "insert into T_ACCOUNT_CREDIT_CARD (ACCOUNT_ID, NUMBER, VERSION) values (?, ?, 0)";
                    jdbcTemplate.update(insertCCSql, accid, cc);

                }

                return null;
            }
        });

    }
}
