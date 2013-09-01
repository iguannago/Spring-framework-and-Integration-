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

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.xml.transform.StringSource;
import rewards.RewardConfirmation;

import javax.xml.transform.Source;

@ContextConfiguration("marshalling-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class MarshallingIntegrationTests extends MarshallingTestBase {

    @Autowired
    MessagingTemplate template;

    /**
     * Template method to perform the marshalling we want to test
     *
     * @param confirmation
     * @return source
     */
    @Override
    protected Source doMarshallRewardConfirmation(RewardConfirmation confirmation) {
        template.convertAndSend("confirmations", confirmation);

        String xmlConfStr = (String) template.receiveAndConvert("xmlConfirmations");

        return new StringSource(xmlConfStr);
    }

}
