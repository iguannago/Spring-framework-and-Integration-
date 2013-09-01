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

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.xml.xpath.Jaxp13XPathTemplate;
import org.springframework.xml.xpath.XPathOperations;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;

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
     * @throws java.io.IOException
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

        final XPathOperations xpathTemplate = new Jaxp13XPathTemplate();

        Object receivedPayload = doUnmarshallDining(xmlFile);

        // TODO-01 Assert that the received message payload is a Dining, with values corresponding to input file attributes (use xpathTemplate)
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
