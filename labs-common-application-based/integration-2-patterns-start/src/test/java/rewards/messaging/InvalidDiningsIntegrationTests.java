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
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import rewards.Dining;
import rewards.RewardNetwork;

import java.util.Date;


@ContextConfiguration("test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class InvalidDiningsIntegrationTests {

	Dining invalidDining = new Dining(-1f, null, null, (Date) null);

	@Autowired RewardNetwork rewardNetwork;
	@Autowired MessagingTemplate template;

	@Test
	public void invalidDiningShouldCauseExceptionMessage() throws Exception {
		// set up mock for when there's a mistake in the filter config,
		// the invalid dining would cause an exception then
		when(rewardNetwork.rewardAccountFor(invalidDining)).thenThrow(new EmptyResultDataAccessException(1));

        // TODO-06 Send invalidDining to the 'dinings' channel
        template.convertAndSend("dinings", invalidDining);


        // TODO-11 Receive exception from errorTestChannel, and assert that it's a MessageRejectedException
	}
}
