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
import org.springframework.util.StringUtils;
import rewards.Dining;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Base class for concurrency tests
 *
 * @author Dominic North
 */
public abstract class CSVDiningBatchProcessorBase implements DiningBatchProcessor {
    /**
     * logger
     */
    private final Logger logger = Logger.getLogger("rewards");

    /**
     * Date-time format for parsing
     */
    protected final DateFormat dateTimeFormat;

    /**
     * Constructor taking string format
     *
     * @param dateTimeFormatString
     */
    public CSVDiningBatchProcessorBase(String dateTimeFormatString) {
        this.dateTimeFormat = new SimpleDateFormat(dateTimeFormatString);
    }

    /**
     * Return a dining object created by parsing one record of CSV file
     *
     * @param csvRecord
     * @return dining
     */
    protected Dining createDiningFromCsv(String csvRecord) {
        String[] fields = StringUtils.commaDelimitedListToStringArray(csvRecord);
        float amount = Float.valueOf(fields[0]);
        String creditCardNumber = fields[1];
        String merchantNumber = fields[2];
        Date date = null;
        try {
            date = getDateTimeFormat().parse(fields[3]);
        } catch (ParseException pe) {
            throw new RuntimeException("error parsing date-time for record'" + csvRecord + "'", pe);
        }

        return new Dining(amount, creditCardNumber, merchantNumber, date);
    }

    /**
     * @return logger
     */
    protected Logger getLogger() {
        return logger;
    }

    /**
     * @return Date-Time Format
     */
    protected DateFormat getDateTimeFormat() {
        return dateTimeFormat;
    }
}
