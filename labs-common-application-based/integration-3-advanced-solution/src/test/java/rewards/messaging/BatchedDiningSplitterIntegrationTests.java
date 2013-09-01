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

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.xml.transform.StringSource;
import org.springframework.xml.xpath.Jaxp13XPathTemplate;
import org.springframework.xml.xpath.XPathOperations;

import java.io.File;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class BatchedDiningSplitterIntegrationTests {

    private XPathOperations xpathTemplate = new Jaxp13XPathTemplate();

    @Autowired
    private MessagingTemplate template;

    @Test
    public void inboundSingleDiningXml() throws Exception {
        File diningFile = new ClassPathResource("dining-sample.xml", getClass()).getFile();
        template.convertAndSend("mixedXmlDinings", diningFile);

        Object receivedPayload = template.receive("xmlDinings").getPayload();
        assertTrue(receivedPayload instanceof String);
        StringSource receivedXml = new StringSource((String) receivedPayload);
        assertThat(xpathTemplate.evaluateAsDouble("count(/dining)", receivedXml), is(1.0));
        assertThat(xpathTemplate.evaluateAsDouble("/dining/amount/@value", receivedXml), is(25.0));
        assertThat(xpathTemplate.evaluateAsString("/dining/creditcard/@number", receivedXml),
                          is("1234123412340001"));
        assertThat(xpathTemplate.evaluateAsString("/dining/merchant/@number", receivedXml), is("1234567890"));
        assertThat(xpathTemplate.evaluateAsString("/dining/timestamp/date", receivedXml), is("2012-01-21"));
        assertThat(xpathTemplate.evaluateAsString("/dining/timestamp/time", receivedXml), is("18:40:00"));

        receivedPayload = template.receiveAndConvert("xmlDinings");
        assertNull("no 2nd message", receivedPayload);
    }

    @Test
    public void inboundMultipleDiningXml() throws Exception {
        File diningsFile = new ClassPathResource("dinings-sample.xml", getClass()).getFile();
        template.convertAndSend("mixedXmlDinings", diningsFile);

        Object receivedPayload = template.receiveAndConvert("xmlDinings");
        assertTrue(receivedPayload instanceof String);
        StringSource receivedXml = new StringSource((String) receivedPayload);
        assertThat(xpathTemplate.evaluateAsDouble("count(/dining)", receivedXml), is(1.0));
        assertThat(xpathTemplate.evaluateAsDouble("/dining/amount/@value", receivedXml), is(10.5));
        assertThat(xpathTemplate.evaluateAsString("/dining/creditcard/@number", receivedXml),
                          is("1234123412340003"));
        assertThat(xpathTemplate.evaluateAsString("/dining/merchant/@number", receivedXml), is("1234567890"));
        assertThat(xpathTemplate.evaluateAsString("/dining/timestamp/date", receivedXml), is("2012-01-21"));
        assertThat(xpathTemplate.evaluateAsString("/dining/timestamp/time", receivedXml), is("09:00:00"));

        receivedPayload = template.receiveAndConvert("xmlDinings");
        assertTrue(receivedPayload instanceof String);
        receivedXml = new StringSource((String) receivedPayload);
        assertThat(xpathTemplate.evaluateAsDouble("count(/dining)", receivedXml), is(1.0));
        assertThat(xpathTemplate.evaluateAsDouble("/dining/amount/@value", receivedXml), is(100.0));
        assertThat(xpathTemplate.evaluateAsString("/dining/creditcard/@number", receivedXml),
                          is("1234123412341234"));
        assertThat(xpathTemplate.evaluateAsString("/dining/merchant/@number", receivedXml), is("1234567890"));
        assertThat(xpathTemplate.evaluateAsString("/dining/timestamp/date", receivedXml), is("2012-01-21"));
        assertThat(xpathTemplate.evaluateAsString("/dining/timestamp/time", receivedXml), is("19:00:00"));

        receivedPayload = template.receiveAndConvert("xmlDinings");
        assertNull("no 3rd message", receivedPayload);
    }
}
