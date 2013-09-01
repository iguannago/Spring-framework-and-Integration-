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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.xml.xpath.Jaxp13XPathTemplate;
import org.springframework.xml.xpath.XPathOperations;
import rewards.Dining;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author Dominic North
 */
public abstract class UnmarshallingTestBase {

    /**
     * xmlFile to unmarshall
     */
    private File xmlFile = null;

    /**
     * Get the file ready
     *
     * @throws IOException
     */
    @Before
    public void setUp() throws IOException {
        xmlFile = new ClassPathResource("dining-sample.xml", getClass()).getFile();
    }


    /**
     * Run test letting subclass get the unmarshalled dining object it's way
     *
     * @throws Exception
     */
    @Test
    public void unmarshallDiningXml() throws Exception {
        final Source inputXml = new StreamSource(xmlFile);

        Object receivedPayload = doUnmarshallDining(xmlFile);

        Assert.assertTrue("should be a Dining:receivedPayload.getClass()=" + receivedPayload.getClass(),
                          receivedPayload instanceof Dining);

        final XPathOperations xpathTemplate = new Jaxp13XPathTemplate();

        final DateFormat df = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        Dining expectedDining =
                new Dining((float) xpathTemplate.evaluateAsDouble("//amount/@value", inputXml),
                           xpathTemplate.evaluateAsString("//creditcard/@number", inputXml),
                           xpathTemplate.evaluateAsString("//merchant/@number", inputXml),
                           df.parse(
                                   xpathTemplate.evaluateAsString("//date", inputXml) +
                                   xpathTemplate.evaluateAsString("//time", inputXml)));

        Assert.assertEquals("received dining should equal expected dining", expectedDining, receivedPayload);
    }

    /**
     * Template method for doing the unmarshalling we want to test
     *
     * @param xmlFile
     * @return
     * @throws java.io.IOException
     */
    protected abstract Object doUnmarshallDining(File xmlFile) throws IOException;
}
