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

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.core.PollableChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class InboundFileDiningIntegrationTests {

	@Autowired PollableChannel xmlConfirmations;

	@Test
    @Ignore // TODO-11 remove @Ignore
	public void filesReceived() throws Exception {
		int messageCount = 0;
		for(;;) {
			Message<?> receivedMessage = xmlConfirmations.receive(2500);
			if (receivedMessage == null) {
				break;
			}
			messageCount++;
			assertThat(receivedMessage.getPayload(), instanceOf(String.class));
		}
		assertEquals(3, messageCount);
	}

}
