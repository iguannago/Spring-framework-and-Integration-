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

/**
 *
 */
package rewards.batch;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import rewards.Dining;

/**
 * FieldSetMapper to create Dining objects
 *
 * @author djnorth
 */
public class DiningFieldSetMapper implements FieldSetMapper<Dining> {

    /**
     * Map fieldSet to Dining object
     *
     * @param fieldSet
     * @return Dining object
     * @see org.springframework.batch.item.file.mapping.FieldSetMapper
     */
    @Override
    public Dining mapFieldSet(FieldSet fieldSet) throws BindException {
        return new Dining(fieldSet.readFloat("amount"),
                          fieldSet.readString("creditCardNumber"),
                          fieldSet.readString("merchantNumber"),
                          fieldSet.readDate("date", "yyyy-MM-dd"));
    }

}
