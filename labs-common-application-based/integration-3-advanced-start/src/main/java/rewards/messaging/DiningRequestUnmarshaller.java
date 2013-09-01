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

package rewards.messaging;

import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.XmlMappingException;
import org.springframework.xml.xpath.Jaxp13XPathTemplate;
import org.springframework.xml.xpath.XPathOperations;
import rewards.Dining;

import javax.xml.transform.Source;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DiningRequestUnmarshaller implements Unmarshaller {

    private XPathOperations xpathTemplate = new Jaxp13XPathTemplate();

    /**
     * If we prefer to make this configurable, we should create a constructor, and inject the format string
     */
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(Dining.class);
    }

    @Override
    public Object unmarshal(Source source) throws XmlMappingException,
                                                  IOException {

        double amount = xpathTemplate.evaluateAsDouble(
                "/dining/amount/@value", source);
        String creditCardNumber = xpathTemplate.evaluateAsString(
                "/dining/creditcard/@number", source);
        String merchantNumber = xpathTemplate.evaluateAsString(
                "/dining/merchant/@number", source);
        String dateString = xpathTemplate.evaluateAsString(
                "/dining/timestamp/date", source);
        String timeString = xpathTemplate.evaluateAsString(
                "/dining/timestamp/time", source);
        try {
            Dining dining = new Dining((float) amount, creditCardNumber, merchantNumber,
                                       dateFormat.parse(dateString + timeString));
            return dining;
        } catch (ParseException pe) {
            throw new UnmarshallingFailureException("invalid timestamp in msg:" + source.toString(), pe);
        }

    }

}
