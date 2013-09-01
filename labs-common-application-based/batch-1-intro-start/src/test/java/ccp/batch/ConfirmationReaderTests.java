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

package ccp.batch;

import ccp.Confirmation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.batch.item.ParseException;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.BytesMessage;
import javax.jms.MapMessage;
import javax.jms.TextMessage;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test to check the ConfirmationReader implementation 
 * for both valid and invalid Messages.
 */
public class ConfirmationReaderTests {

	private JmsTemplate jmsTemplate;
	private ConfirmationReader confirmationReader;

	private static final String UU_ID = UUID.randomUUID().toString();
	private static final String VALID_XML = "<?xml version=\"1.0\"?>\n" 
		+ "<reward-confirmation xmlns=\"" + ConfirmationReader.NS + "\" dining-transaction-id=\"" + UU_ID + "\"/>";

	@Before
	public void setup() {
		jmsTemplate = mock(JmsTemplate.class);
		when(jmsTemplate.getReceiveTimeout()).thenReturn(JmsTemplate.RECEIVE_TIMEOUT_NO_WAIT);
		when(jmsTemplate.getDefaultDestinationName()).thenReturn("someDestination");
		confirmationReader = new ConfirmationReader(jmsTemplate);
	}

	@Test
	public void processValidTextMessage() throws Exception {
		TextMessage textMessage = mock(TextMessage.class);
		when(jmsTemplate.receive()).thenReturn(textMessage);
		when(textMessage.getText()).thenReturn(VALID_XML);

		Confirmation confirmation = confirmationReader.read();
		assertEquals(UU_ID, confirmation.getTransactionId());
	}

	@Test
	public void processValidBytesMessage() throws Exception {
		BytesMessage bytesMessage = mock(BytesMessage.class);
		when(jmsTemplate.receive()).thenReturn(bytesMessage);
		final int length = VALID_XML.getBytes().length;
		when(bytesMessage.getBodyLength()).thenReturn((long) length);
		when(bytesMessage.readBytes((byte[]) anyObject())).thenAnswer(new Answer<Integer>() {
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				System.arraycopy(VALID_XML.getBytes(), 0, invocation.getArguments()[0], 0, length);
				return length;
			}
		});

		Confirmation confirmation = confirmationReader.read();
		assertEquals(UU_ID, confirmation.getTransactionId());
	}

	@Test
	public void processEmptyQueue() throws Exception {
		when(jmsTemplate.receive()).thenReturn(null);
		
		assertNull(confirmationReader.read());
	}

	@Test(expected=ParseException.class)
	public void processInvalidTextMessage() throws Exception {
		TextMessage textMessage = mock(TextMessage.class);
		when(jmsTemplate.receive()).thenReturn(textMessage);
		when(textMessage.getText()).thenReturn("<not-what-we-expected/>");
		
		confirmationReader.read();
	}

	@Test(expected=ParseException.class)
	public void processMapMessage() throws Exception {
		MapMessage mapMessage = mock(MapMessage.class);
		when(jmsTemplate.receive()).thenReturn(mapMessage);
		
		confirmationReader.read();
	}

}