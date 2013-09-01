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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.springframework.xml.xpath.Jaxp13XPathTemplate;
import org.springframework.xml.xpath.XPathOperations;

import rewards.RewardConfirmation;

import java.io.IOException;

public class RewardConfirmationMarshallerTest extends MarshallingTestBase {

	RewardConfirmationMarshaller marshaller = new RewardConfirmationMarshaller();

	XPathOperations xpathTemplate = new Jaxp13XPathTemplate();

    /**
     * Template method to perform the marshalling we want to test. Here we directly marshall a reward object
     *
     * @param confirmation
     * @return source
     */
    @Override
    protected Source doMarshallRewardConfirmation(RewardConfirmation confirmation) throws IOException {
        DOMResult result = new DOMResult();
        marshaller.marshal(confirmation, result);
        return new DOMSource(result.getNode());
    }
}
