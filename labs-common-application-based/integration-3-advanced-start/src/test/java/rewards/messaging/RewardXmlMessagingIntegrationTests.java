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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class RewardXmlMessagingIntegrationTests {

    @Autowired
    private MessagingTemplate template;

    @Test
    @Ignore // TODO-05 Remove @Ignore
    public void sendXmlDiningTwice() throws Exception {
        File xmlFile = new ClassPathResource("dining-sample.xml", getClass()).getFile();
        template.convertAndSend("xmlDinings", xmlFile);

        Object payload1 = template.receiveAndConvert("xmlConfirmations");
        assertThat("payload1=" + payload1, payload1, instanceOf(String.class));

        template.convertAndSend("xmlDinings", xmlFile);

        Object payload2 = template.receiveAndConvert("xmlConfirmations");
        assertThat("payload2=" + payload2, payload2, instanceOf(String.class));
    }

    @Test
    @Ignore // TODO-10 Remove @Ignore
    public void sendMultipleDinings() throws Exception {
        File xmlFile = new ClassPathResource("dinings-sample.xml", getClass()).getFile();
        template.convertAndSend("mixedXmlDinings", xmlFile);

        Object payload1 = template.receiveAndConvert("xmlConfirmations");
        assertThat("payload1=" + payload1, payload1, instanceOf(String.class));

        Object payload2 = template.receiveAndConvert("xmlConfirmations");
        assertThat("payload2=" + payload2, payload2, instanceOf(String.class));

        Object payload3 = template.receiveAndConvert("xmlConfirmations");
        assertNull("payload3=" + payload3, payload3);
    }

}
